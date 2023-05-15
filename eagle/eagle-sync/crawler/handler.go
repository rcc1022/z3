package crawler

import (
	"bytes"
	"crypto/tls"
	"fmt"
	"io"
	"io/ioutil"
	"net/http"
	"strings"
	"time"
)

type handler struct {
	protocol string
	method   string
	uri      string
	header   map[string]string
	uid      string
	ver      string
	langx    string
	gq       string
	gqbd     string
	jra      string
	jr       string
	jrbd     string
	sjb      string
	sjbd     string
	count    int
	duration float64
}

func (h *handler) ServeHTTP(writer http.ResponseWriter, req *http.Request) {
	if req.RequestURI == "/show-crawler-info" {
		show(writer, req, h)

		return
	}

	if req.RequestURI == "/bet-to-hg-test" {
		success, _ := h.betOrderView2(req)
		writer.WriteHeader(200)
		writer.Write([]byte(fmt.Sprintf(`{"success":%t}`, success)))

		return
	}

	if req.RequestURI == "/bet-to-hg" {
		success := h.bet(req)
		writer.WriteHeader(200)
		writer.Write([]byte(fmt.Sprintf(`{"success":%t}`, success)))

		return
	}

	Log("forward to %s:%s%s%s", req.Method, h.protocol, cfg.To, req.RequestURI)
	defer req.Body.Close()

	b, err := ioutil.ReadAll(req.Body)
	if err != nil {
		Log("read request %s body err %v", req.RequestURI, err)

		return
	}

	request, err := http.NewRequest(req.Method, h.protocol+cfg.To+req.RequestURI, bytes.NewBuffer(b))
	if err != nil {
		Log("new request [%s:%s%s%s] err %v", req.Method, h.protocol, cfg.To, req.RequestURI, err)

		return
	}

	for key := range req.Header {
		request.Header.Set(key, strings.ReplaceAll(req.Header.Get(key), req.Host, cfg.To))
	}
	request.Header.Set("Host", req.Host)
	request.Header.Del("Accept-Encoding")
	request.Header.Del("Content-Length")

	client := http.Client{
		Timeout:   time.Minute,
		Transport: &http.Transport{TLSClientConfig: &tls.Config{InsecureSkipVerify: true}},
	}
	res, err := client.Do(request)
	if err != nil {
		Log("do request [%s:%s%s] err %v", req.Method, cfg.To, req.RequestURI, err)

		return
	}
	defer res.Body.Close()

	for key := range res.Header {
		writer.Header().Set(key, res.Header.Get(key))
	}

	if strings.HasPrefix(req.RequestURI, "/transform.php?ver=") {
		h.transform(writer, req, res, string(b))

		return
	}

	writer.WriteHeader(res.StatusCode)
	io.Copy(writer, res.Body)
}
