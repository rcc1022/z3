package crawler

import (
	"crypto/tls"
	"errors"
	"io/ioutil"
	"net/http"
	"strings"
	"time"
)

func (h *handler) doHTTP(param string) ([]byte, error) {
	if h.uri == "" {
		return nil, errors.New("uri is empty")
	}

	req, err := http.NewRequest(h.method, h.protocol+cfg.To+h.uri, strings.NewReader(param))
	if err != nil {
		Log("new request [%s:%s%s:%s] err %v", h.method, cfg.To, h.uri, param, err)

		return nil, err
	}

	for key := range h.header {
		req.Header.Set(key, h.header[key])
	}

	client := http.Client{
		Timeout:   time.Minute,
		Transport: &http.Transport{TLSClientConfig: &tls.Config{InsecureSkipVerify: true}},
	}
	res, err := client.Do(req)
	if err != nil {
		Log("do request [%s:%s%s:%s] err %v", h.method, cfg.To, h.uri, param, err)

		return nil, err
	}
	defer res.Body.Close()

	body, err := ioutil.ReadAll(res.Body)
	if err != nil {
		Log("read from [%s:%s%s:%s] err %v", h.method, cfg.To, h.uri, param, err)

		return nil, err
	}

	return body, err
}
