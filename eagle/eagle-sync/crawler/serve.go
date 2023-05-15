package crawler

import (
	"net/http"
)

func Serve() {
	for _, p := range cfg.Push {
		go push(p)
	}

	go http.ListenAndServe(":80", &handler{protocol: "http://"})

	h := handler{
		protocol: "https://",
		gq:       "uid=%s&ver=%s&langx=%s&p=get_game_list&p3type=&date=&gtype=ft&showtype=live&rtype=rb&ltype=3&sorttype=L&specialClick=&isFantasy=N&ts=%d",
		gqbd:     "uid=%s&ver=%s&langx=%s&p=get_game_list&p3type=&date=&gtype=ft&showtype=live&rtype=rpd&ltype=3&sorttype=L&specialClick=&isFantasy=N&ts=%d",
		jra:      "p=get_league_list_All&uid=%s&ver=%s&langx=%s&gtype=FT&FS=N&showtype=ft&date=0&ts=%d&nocp=N",
		jr:       "uid=%s&ver=%s&langx=%s&p=get_game_list&p3type=&date=0&gtype=ft&showtype=today&rtype=r&ltype=3&lid=%s&action=clickCoupon&sorttype=L&specialClick=&isFantasy=N&ts=%d",
		jrbd:     "uid=%s&ver=%s&langx=%s&p=get_game_list&p3type=&date=0&gtype=ft&showtype=today&rtype=pd&ltype=3&lid=%s&action=clickCoupon&sorttype=L&specialClick=&isFantasy=N&ts=%d",
		sjb:      "uid=%s&ver=%s&langx=%s&p=get_cup_list&p3type=&date=&gtype=ft&showtype=today&rtype=r&ltype=3&sorttype=T&specialClick=special&isFantasy=N&team_id=&ts=%d",
		sjbd:     "uid=%s&ver=%s&langx=%s&p=get_cup_list&p3type=&date=&gtype=ft&showtype=today&rtype=pd&ltype=3&sorttype=T&specialClick=special&isFantasy=N&team_id=&ts=%d",
	}
	go fetch(&h)
	http.ListenAndServe(":443", &h)
}
