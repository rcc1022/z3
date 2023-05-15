package crawler

import (
	"encoding/xml"
	"fmt"
	"strconv"
	"time"
)

type timer struct {
	Timestamp string `xml:"systimestamp"`
}

var serverTime int64 = 0
var localTime int64 = 0

func (h *handler) timestamp() int64 {
	if localTime > 0 {
		return serverTime + time.Now().UnixMilli() - localTime
	}

	b, err := h.doHTTP(fmt.Sprintf("p=get_systemTime&ver=%s&&uid=%s&langx=zh-cn", h.ver, h.uid))
	if err != nil {
		return 0
	}

	t := timer{}
	if xml.Unmarshal(b, &t) != nil {
		return 0
	}

	serverTime, err = strconv.ParseInt(t.Timestamp+"000", 10, 64)
	if err != nil {
		return 0
	}

	serverTime += 12 * time.Hour.Milliseconds()
	localTime = time.Now().UnixMilli()

	return serverTime
}
