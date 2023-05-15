package crawler

import (
	"encoding/xml"
	"fmt"
	"net/http"
	"strings"
	"time"
)

type betres struct {
	Gid      string
	Chose    string
	Wtype    string
	Rtype    string
	Golds    int
	Con      string  `xml:"con"`
	Ratio    string  `xml:"ratio"`
	Ts       string  `xml:"ts"`
	TicketID string  `xml:"ticket_id"`
	Wagers   []wager `xml:"wagers"`
}

type wager struct {
	WID string `xml:"w_id"`
}

func (h *handler) bet(req *http.Request) bool {
	req.ParseForm()
	ok, br := h.betOrderView2(req)
	if !ok {
		return false
	}

	param := fmt.Sprintf("p=FT_bet&uid=%s&ver=%s&langx=zh-cn&odd_f_type=H&golds=%d&gid=%s&gtype=FT&wtype=%s&rtype=%s&chose_team=%s&ioratio=%s&con=%s&ratio=%s&autoOdd=Y&timestamp=%d&timestamp2=%s&isRB=Y&imp=N&ptype=&isYesterday=N&f=1R",
		h.uid, h.ver, br.Golds, br.Gid, br.Wtype, br.Rtype, br.Chose, req.FormValue("rate"), br.Con, br.Ratio, h.timestamp(), br.Ts)
	b, err := h.doHTTP(param)
	if err != nil {
		Log("bet: read bet %s err %v", param, err)

		return false
	}

	if err = xml.Unmarshal(b, &br); err != nil {
		Log("bet: unmarshal bet response %s:%s err %v", param, string(b), err)

		return false
	}

	if br.TicketID == "" {
		Log("bet: no bet ticket id %s:%s", param, string(b))

		return false
	}

	tid := br.TicketID
	for i := 0; i < 5; i++ {
		time.Sleep(time.Second)
		param = fmt.Sprintf("p=get_today_wagers&uid=%s&langx=zh-cn&LS=g&selGtype=ALL&chk_cw=N&ts=%d", h.uid, h.timestamp())
		b, err = h.doHTTP(param)
		if err != nil {
			Log("bet: read wagers %s err %v", param, err)

			return false
		}

		if err = xml.Unmarshal(b, &br); err != nil {
			Log("bet: unmarshal wagers response %s:%s err %v", param, string(b), err)

			return false
		}

		for _, w := range br.Wagers {
			if strings.HasSuffix(w.WID, tid) {
				return true
			}
		}
	}

	return false
}

func (h *handler) betOrderView2(req *http.Request) (bool, *betres) {
	req.ParseForm()
	br := betres{
		Gid:   req.FormValue("gid"),
		Chose: req.FormValue("chose"),
		Golds: 50,
	}
	var wtype, rtype string
	br.Wtype, br.Rtype, wtype, rtype = betType(req.FormValue("type"), br.Chose)
	if br.Wtype == "RPD" {
		br.Chose = br.Rtype
		br.Golds = 20
	}

	if !h.betOrderView(&br) {
		return false, nil
	}

	if br.Con == "" || br.Ratio == "" {
		br.Wtype = wtype
		br.Rtype = rtype
		if wtype == "PD" {
			br.Chose = rtype
		}
		if !h.betOrderView(&br) {
			return false, nil
		}
	}

	if br.Con == "" || br.Ratio == "" {
		return false, nil
	}

	return true, &br
}

func (h *handler) betOrderView(br *betres) bool {
	param := fmt.Sprintf("p=FT_order_view&uid=%s&ver=%s&langx=zh-cn&odd_f_type=H&gid=%s&gtype=FT&wtype=%s&chose_team=%s", h.uid, h.ver, br.Gid, br.Wtype, br.Chose)
	b, err := h.doHTTP(param)
	if err != nil {
		Log("bet: read prebet %s err %v", param, err)

		return false
	}

	if err = xml.Unmarshal(b, br); err != nil {
		Log("bet: unmarshal bet response %s:%s err %v", param, string(b), err)

		return false
	}

	if br.Con == "" || br.Ratio == "" {
		Log("bet: con or ratio empty: %s", param)
		Log("bet: con or ratio empty: %s", string(b))
	}

	return true
}

func betType(t, chose string) (string, string, string, string) {
	switch t {
	case "rang-qiu":
		return "RE", "RE" + chose, "R", "R" + chose
	case "de-fen":
		if chose == "H" {
			chose = "O"
		} else {
			chose = "U"
		}
		return "ROU", "ROU" + chose, "OU", "OU" + chose
	case "du-ying":
		return "RM", "RM" + chose, "M", "M" + chose
	case "rang-qiu-shang":
		return "HRE", "HRE" + chose, "HR", "HR" + chose
	case "de-fen-shang":
		if chose == "H" {
			chose = "O"
		} else {
			chose = "U"
		}
		return "HROU", "HROU" + chose, "HOU", "HOU" + chose
	case "du-ying-shang":
		return "HRM", "HRM" + chose, "HM", "HM" + chose
	case "bo-dan":
		return "RPD", "RH" + strings.ReplaceAll(chose, "-", "C"), "PD", "H" + strings.ReplaceAll(chose, "-", "C")
	default:
		return "", "", "", ""
	}
}
