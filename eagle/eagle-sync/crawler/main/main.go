package main

import (
	"crawler"
)

func main() {
	if err := crawler.Load(); err != nil {
		crawler.Log("load config err %v", err)

		return
	}

	crawler.Serve()
}
