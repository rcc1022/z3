package crawler

import (
	"fmt"
	"net/http"
)

var html = `<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Crawler</title>
  </head>
  <body>
  <div>%s</div>
  <hr/>
  %s  <hr/>
  <div>uid:%s</div>
  <div>ver:%s</div>
  <div>langx:%s</div>
  </body>
  <script type="text/javascript">
  setTimeout(()=>location.reload(), 1000);
  </script>
</html>`

func show(writer http.ResponseWriter, req *http.Request, h *handler) {
	defer req.Body.Close()

	writer.WriteHeader(200)
	header := ""
	for key := range h.header {
		header += fmt.Sprintf("  <div>%s=%s</div>\n", key, h.header[key])
	}
	writer.Write([]byte(fmt.Sprintf(html, h.uri, header, h.uid, h.ver, h.langx)))
}
