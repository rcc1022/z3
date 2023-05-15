package crawler

import (
	"bytes"
	"io/ioutil"
	"net/http"
	"net/url"
	"strconv"
	"strings"
)

func (h *handler) transform(writer http.ResponseWriter, req *http.Request, res *http.Response, body string) {
	b, err := ioutil.ReadAll(res.Body)
	if err != nil {
		Log("read body err %v", err)

		return
	}

	b = bytes.ReplaceAll(b, []byte(cfg.To), []byte(req.Host))
	writer.Header().Set("Content-Length", strconv.Itoa(len(b)))
	writer.WriteHeader(res.StatusCode)
	writer.Write(b)

	if strings.Contains(body, "p=FT_order_view") || strings.Contains(body, "p=FT_bet") {
		Log("bet: %s", body)
		Log("bet: %s", string(b))

		return
	}

	// Log("transform parameter %s", body)
	// if !strings.Contains(body, "p=get_game_list") && !strings.Contains(body, "p=get_league_list_All") && !strings.Contains(body, "p=get_cup_list") {
	// 	return
	// }

	h.method = req.Method
	h.uri = req.RequestURI

	header := make(map[string]string)
	for key := range req.Header {
		header[key] = req.Header.Get(key)
	}
	header["Host"] = cfg.To
	header["Origin"] = h.protocol + cfg.To
	header["Referer"] = h.protocol + cfg.To + "/"
	delete(header, "Accept-Encoding")
	delete(header, "Content-Length")
	h.header = header

	values, err := url.ParseQuery(body)
	if err != nil {
		return
	}

	// if values.Has("ts") {
	// 	if value := values.Get("ts"); value != "" {
	// 		body = strings.ReplaceAll(body, value, "%d")
	// 	}
	// }
	if values.Has("uid") {
		h.uid = values.Get("uid")
	}
	if values.Has("ver") {
		h.ver = values.Get("ver")
	}
	if values.Has("langx") {
		h.langx = values.Get("langx")
	}

	// if strings.Contains(body, "p=get_league_list_All") && strings.Contains(body, "gtype=FT") && strings.Contains(body, "showtype=ft") && bytes.Contains(b, []byte("<name>今日赛事</name><lid>")) {
	// 	h.jra = body
	// } else if strings.Contains(body, "gtype=ft") {
	// 	if strings.Contains(body, "showtype=live") {
	// 		if strings.Contains(body, "rtype=rb") {
	// 			h.gq = body
	// 		} else if strings.Contains(body, "rtype=rpd") {
	// 			h.gqbd = body
	// 		}
	// 	} else if strings.Contains(body, "showtype=today") {
	// 		if strings.Contains(body, "p=get_cup_list") {
	// 			if strings.Contains(body, "rtype=r") {
	// 				h.sjb = body
	// 			} else if strings.Contains(body, "rtype=pd") {
	// 				h.sjbd = body
	// 			}
	// 		} else if index := strings.Index(body, "lid="); index > -1 {
	// 			lid := body[index+4:]
	// 			if index = strings.Index(lid, "&"); index > -1 {
	// 				body = strings.ReplaceAll(body, lid[:index], "%s")
	// 				if strings.Contains(body, "rtype=r") {
	// 					h.jr = body
	// 				} else if strings.Contains(body, "rtype=pd") {
	// 					h.jrbd = body
	// 				}
	// 			}
	// 		}
	// 	}
	// }
}
