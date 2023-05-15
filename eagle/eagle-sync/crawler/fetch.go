package crawler

import (
	"bytes"
	"encoding/json"
	"encoding/xml"
	"errors"
	"fmt"
	"strings"
	"time"
)

type fdata struct {
	Ec []struct {
		Game game `xml:"game"`
	} `xml:"ec"`
}

type game struct {
	Gid             string `xml:"GID" json:"gid"`
	Datetime        string `xml:"DATETIME" json:"datetime"`
	Timer           string `xml:"TIMER" json:"timer"`
	League          string `xml:"LEAGUE" json:"league"`
	TeamH           string `xml:"TEAM_H" json:"teamH"`
	TeamC           string `xml:"TEAM_C" json:"teamC"`
	ScoreH          int    `xml:"SCORE_H" json:"scoreH"`
	ScoreC          int    `xml:"SCORE_C" json:"scoreC"`
	Strong          string `xml:"STRONG" json:"strong"`
	RangQiu         string `xml:"RATIO_RE" json:"rangQiu"`
	RangQiuH        string `xml:"IOR_REH" json:"rangQiuH"`
	RangQiuC        string `xml:"IOR_REC" json:"rangQiuC"`
	RangQiuShang    string `xml:"RATIO_HRE" json:"rangQiuShang"`
	RangQiuShangH   string `xml:"IOR_HREH" json:"rangQiuShangH"`
	RangQiuShangC   string `xml:"IOR_HREC" json:"rangQiuShangC"`
	DeFenH          string `xml:"RATIO_ROUO" json:"deFenH"`
	DeFenC          string `xml:"RATIO_ROUU" json:"deFenC"`
	DeFenRateH      string `xml:"IOR_ROUC" json:"deFenRateH"`
	DeFenRateC      string `xml:"IOR_ROUH" json:"deFenRateC"`
	DeFenShangH     string `xml:"RATIO_HROUO" json:"deFenShangH"`
	DeFenShangC     string `xml:"RATIO_HROUU" json:"deFenShangC"`
	DeFenRateShangH string `xml:"IOR_HROUC" json:"deFenRateShangH"`
	DeFenRateShangC string `xml:"IOR_HROUH" json:"deFenRateShangC"`
	DuYingH         string `xml:"IOR_RMH" json:"duYingH"`
	DuYingC         string `xml:"IOR_RMC" json:"duYingC"`
	DuYingHe        string `xml:"IOR_RMN" json:"duYingHe"`
	DuYingShangH    string `xml:"IOR_HRMH" json:"duYingShangH"`
	DuYingShangHC   string `xml:"IOR_HRMC" json:"duYingShangC"`
	DuYingShangHe   string `xml:"IOR_HRMN" json:"duYingShangHe"`
}

var gmap = map[string]string{
	"RATIO_R":    "RATIO_RE",
	"IOR_RH":     "IOR_REH",
	"IOR_RC":     "IOR_REC",
	"RATIO_HR":   "RATIO_HRE",
	"IOR_HRH":    "IOR_HREH",
	"IOR_HRC":    "IOR_HREC",
	"RATIO_OUO":  "RATIO_ROUO",
	"RATIO_OUU":  "RATIO_ROUU",
	"IOR_OUH":    "IOR_ROUH",
	"IOR_OUC":    "IOR_ROUC",
	"RATIO_HOUO": "RATIO_HROUO",
	"RATIO_HOUU": "RATIO_HROUU",
	"IOR_HOUH":   "IOR_HROUH",
	"IOR_HOUC":   "IOR_HROUC",
	"IOR_MH":     "IOR_RMH",
	"IOR_MC":     "IOR_RMC",
	"IOR_MN":     "IOR_RMN",
	"IOR_HMH":    "IOR_HRMH",
	"IOR_HMC":    "IOR_HRMC",
	"IOR_HMN":    "IOR_HRMN",
}

type bdata struct {
	Ec []struct {
		Game bodan `xml:"game"`
	} `xml:"ec"`
}

type bodan struct {
	Gid  string `xml:"GID" json:"gid"`
	H0C0 string `xml:"IOR_RH0C0" json:"h0c0"`
	H0C1 string `xml:"IOR_RH0C1" json:"h0c1"`
	H0C2 string `xml:"IOR_RH0C2" json:"h0c2"`
	H0C3 string `xml:"IOR_RH0C3" json:"h0c3"`
	H0C4 string `xml:"IOR_RH0C4" json:"h0c4"`
	H1C0 string `xml:"IOR_RH1C0" json:"h1c0"`
	H1C1 string `xml:"IOR_RH1C1" json:"h1c1"`
	H1C2 string `xml:"IOR_RH1C2" json:"h1c2"`
	H1C3 string `xml:"IOR_RH1C3" json:"h1c3"`
	H1C4 string `xml:"IOR_RH1C4" json:"h1c4"`
	H2C0 string `xml:"IOR_RH2C0" json:"h2c0"`
	H2C1 string `xml:"IOR_RH2C1" json:"h2c1"`
	H2C2 string `xml:"IOR_RH2C2" json:"h2c2"`
	H2C3 string `xml:"IOR_RH2C3" json:"h2c3"`
	H2C4 string `xml:"IOR_RH2C4" json:"h2c4"`
	H3C0 string `xml:"IOR_RH3C0" json:"h3c0"`
	H3C1 string `xml:"IOR_RH3C1" json:"h3c1"`
	H3C2 string `xml:"IOR_RH3C2" json:"h3c2"`
	H3C3 string `xml:"IOR_RH3C3" json:"h3c3"`
	H3C4 string `xml:"IOR_RH3C4" json:"h3c4"`
	H4C0 string `xml:"IOR_RH4C0" json:"h4c0"`
	H4C1 string `xml:"IOR_RH4C1" json:"h4c1"`
	H4C2 string `xml:"IOR_RH4C2" json:"h4c2"`
	H4C3 string `xml:"IOR_RH4C3" json:"h4c3"`
	H4C4 string `xml:"IOR_RH4C4" json:"h4c4"`
}

func fetch(h *handler) {
	for i := 0; true; i++ {
		time.Sleep(time.Second)
		t := time.Now()
		var err error
		if cfg.Fetch == "gunqiu" {
			err = gunqiu(h, i%2)
		} else if cfg.Fetch == "jinri" {
			err = jinri(h, i%2+2)
		} else if cfg.Fetch == "shijiebei" {
			err = shijiebei(h, i%2+4)
		}
		if err == nil {
			h.count++
			h.duration += time.Since(t).Seconds()
		}

		if i%60 > 0 || h.count == 0 {
			continue
		}

		Log("fetch:%s:duration=%.3f;count=%d;avg=%.3f", cfg.Fetch, h.duration, h.count, h.duration/float64(h.count))
		h.count = 0
		h.duration = 0
	}
}

func gunqiu(h *handler, push int) error {
	if push == 0 {
		return fetchGames(h, fmt.Sprintf(h.gq, h.uid, h.ver, h.langx, time.Now().UnixMilli()), push)
	}

	return fetchBodan(h, fmt.Sprintf(h.gqbd, h.uid, h.ver, h.langx, time.Now().UnixMilli()), push)
}

func jinri(h *handler, push int) error {
	param := fmt.Sprintf(h.jra, h.uid, h.ver, h.langx, time.Now().UnixMilli())
	body, err := fetchBody(h, param)
	if err != nil {
		return err
	}

	str := string(body)
	jrss := "<name>今日赛事</name><lid>"
	index := strings.Index(str, jrss)
	if index == -1 {
		Log("no today in data %v:%s:%s", h.header, param, str)

		return errors.New("no today")
	}

	str = str[index+len(jrss):]
	index = strings.Index(str, "</lid>")
	if index == -1 {
		Log("no lid in today data %v:%s:%s", h.header, param, string(body))

		return errors.New("no lid int today")
	}

	time.Sleep(time.Second)
	lid := str[:index]
	if push == 2 {
		return fetchGames(h, fmt.Sprintf(h.jr, h.uid, h.ver, h.langx, lid, time.Now().UnixMilli()), 2)
	}

	return fetchBodan(h, fmt.Sprintf(h.jrbd, h.uid, h.ver, h.langx, lid, time.Now().UnixMilli()), 3)
}

func shijiebei(h *handler, push int) error {
	if push == 4 {
		return fetchGames(h, fmt.Sprintf(h.sjb, h.uid, h.ver, h.langx, time.Now().UnixMilli()), 4)
	}

	return fetchBodan(h, fmt.Sprintf(h.sjbd, h.uid, h.ver, h.langx, time.Now().UnixMilli()), 5)
}

func fetchGames(h *handler, param string, push int) error {
	body, err := fetchBody(h, param)
	if err != nil {
		return err
	}

	if push > 0 {
		for key := range gmap {
			value := gmap[key]
			body = bytes.ReplaceAll(body, []byte("<"+key+">"), []byte("<"+value+">"))
			body = bytes.ReplaceAll(body, []byte("</"+key+">"), []byte("</"+value+">"))
		}
	}

	fd := fdata{}
	if err = xml.Unmarshal(body, &fd); err != nil {
		Log("unmarshal xml [%s:%s] err %v", param, string(body), err)

		return err
	}

	if len(fd.Ec) == 0 {
		Log("no ec in data %v:%s:%s", h.header, param, string(body))

		return nil
	}

	games := make([]game, 0)
	for _, ec := range fd.Ec {
		games = append(games, ec.Game)
	}

	gs, err := json.Marshal(games)
	if err != nil {
		return err
	}

	pushes[push] = string(gs)

	return nil
}

func fetchBodan(h *handler, param string, push int) error {
	body, err := fetchBody(h, param)
	if err != nil {
		return err
	}

	if push > 1 {
		body = bytes.ReplaceAll(body, []byte("IOR_H"), []byte("IOR_RH"))
	}

	bd := bdata{}
	if err = xml.Unmarshal(body, &bd); err != nil {
		Log("unmarshal xml [%s:%s] err %v", param, string(body), err)

		return err
	}

	if len(bd.Ec) == 0 {
		Log("no ec in data %v:%s:%s", h.header, param, string(body))

		return nil
	}

	games := make([]bodan, 0)
	for _, ec := range bd.Ec {
		games = append(games, ec.Game)
	}

	gs, err := json.Marshal(games)
	if err != nil {
		return err
	}

	pushes[push] = string(gs)

	return nil
}

func fetchBody(h *handler, param string) ([]byte, error) {
	if len(param) == 0 {
		Log("fetch no param")

		return nil, errors.New("no param")
	}

	return h.doHTTP(param)
}
