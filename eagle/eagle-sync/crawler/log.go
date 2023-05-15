package crawler

import (
	"io/ioutil"
	"log"
	"os"
	"time"
)

var logPath = "logs/"

func Log(format string, v ...any) {
	log.Printf(format+"\n", v...)
}

func logInit() {
	logFile()
	logClean()
	go func() {
		for {
			now := time.Now()
			if now.Hour() == 0 && now.Minute() == 0 && now.Second() == 0 {
				logFile()
				logClean()
			}
			time.Sleep(time.Second)
		}
	}()
}

func logFile() {
	os.MkdirAll(logPath, os.ModePerm)
	f, err := os.OpenFile(logPath+time.Now().Format("2006-01-02"), os.O_CREATE|os.O_APPEND|os.O_RDWR, os.ModePerm)
	if err != nil {
		log.Printf("opening log file fail %v\n", err)

		return
	}

	log.SetOutput(f)
}

func logClean() {
	files, err := ioutil.ReadDir(logPath)
	if err != nil {
		log.Printf("reading dir [%s] fail %v\n", logPath, err)

		return
	}

	last := time.Now().AddDate(0, 0, -7)
	for _, file := range files {
		if !file.IsDir() && file.ModTime().Before(last) {
			os.Remove(logPath + file.Name())
		}
	}
}
