package crawler

import (
	"encoding/json"
	"io/ioutil"
)

type config struct {
	To    string   `json:"to"`
	Key   string   `json:"key"`
	Fetch string   `json:"fetch"`
	Push  []string `json:"push"`
}

var cfg = config{}

func Load() error {
	b, err := ioutil.ReadFile("config.json")
	if err != nil {
		return err
	}

	if err = json.Unmarshal(b, &cfg); err != nil {
		return err
	}

	Log("load config %v", cfg)

	return nil
}
