package crawler

import (
	"crypto/md5"
	"crypto/tls"
	"fmt"
	"net/http"
	"strings"
	"time"
)

var pushes = []string{"", "", "", "", "", ""}
var pushUri = "/football/saves"

func push(to string) {
	success := 0
	failure := 0
	duration := float64(0)
	for i := 0; true; i++ {
		time.Sleep(500 * time.Millisecond)
		now := time.Now()
		for i := 0; i < 3; i++ {
			if pushTo(to, pushes[2*i], pushes[2*i+1], i) {
				success++
			} else {
				failure++
			}
		}
		duration += time.Since(now).Seconds()
		if i > 0 && i%120 == 0 {
			Log("push:duration=%.3f;success=%d;failure=%d;avg=%.3f", duration, success, failure, duration/float64(success+failure))
			success = 0
			failure = 0
			duration = 0
			i = 0
		}
	}
}

func pushTo(to, games, bodans string, group int) bool {
	if to == "" || games == "" {
		return false
	}

	body := fmt.Sprintf(`bodans=%s&games=%s&group=%d&sign-time=%d&`, bodans, games, group, time.Now().UnixMilli())
	req, err := http.NewRequest("POST", to+pushUri, strings.NewReader(fmt.Sprintf("%ssign=%x", body, md5.Sum([]byte(body+cfg.Key)))))
	if err != nil {
		Log("new push request to %s err %v", to, err)

		return false
	}
	defer req.Body.Close()

	client := http.Client{
		Timeout:   time.Minute,
		Transport: &http.Transport{TLSClientConfig: &tls.Config{InsecureSkipVerify: true}},
	}
	res, err := client.Do(req)
	if err != nil {
		Log("do request %s%s:%s err %v", to, pushUri, body, err)

		return false
	}

	res.Body.Close()

	return true
}
