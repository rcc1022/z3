<script>
import { service, url } from '../util/http';
import header from '@/assets/notice-header.png';
import bell from '@/assets/bell.png';
import marquee from './marquee.vue';
import Pc from './pc.vue';
import Sc from './sc.vue';
import Wu from './wu.vue';
import Football from './football.vue';

export default {
    components: { marquee, Pc, Sc, Wu, Football },
    emits: ['rule'],
    data() {
        return {
            notice: {
                bg: header,
                content: []
            },
            carousel: [],
            marquee: {
                icon: bell,
                content: ""
            },
            games: [],
            game: {}
        };
    },
    mounted() {
        service("/keyvalue/object", { key: "setting.home." }, data => {
            document.title = data["setting.home.title"] || "";
            this.notice.content = (data["setting.home.tan-chu-gong-gao"] || "").split("\n");
            let lunbo = (data["setting.home.lun-bo"] || "").split(",");
            for (let i = 0; i < lunbo.length; i++)
                this.carousel.push(url(lunbo[i]));
            this.marquee.content = data["setting.home.gun-don-gong-gao"] || "";
        });
        this.loadGames();
    },
    methods: {
        loadGames() {
            service("/game/query", { on: 1 }, data => {
                this.games = [];
                for (let i = 0; i < data.list.length; i++) {
                    let game = data.list[i];
                    game.coverUrl = url(game.cover);
                    this.games.push(game);
                }
            });
        },
        showRule() {
            this.$emit('rule', this.game);
        },
    },
}
</script>

<template>
    <div v-if="!game.id">
        <van-swipe class="carousel" :autoplay="3000" indicator-color="white">
            <van-swipe-item v-for="item in carousel" :key="item">
                <img class="image" :src="item" />
            </van-swipe-item>
        </van-swipe>
        <div id="marquee">
            <div class="icon"><img :src="marquee.icon" /></div>
            <van-notice-bar class="marque-notice-bar" color="#ffffff" :text="marquee.content" />
        </div>
        <div id="games">
            <div class="game" v-for="game in games" @click="this.game = game">
                <img :src="game.coverUrl" />
            </div>
        </div>
        <div id="notice-cover" v-if="notice.content.length > 0"></div>
        <div id="notice" v-if="notice.content.length > 0">
            <div class="header" :style="'background-image:url(' + notice.bg + ')'">首页公告</div>
            <div class="content">
                <div v-for="line in notice.content">{{ line }}</div>
            </div>
            <div class="button" @click="notice.content = []">我知道了</div>
        </div>
    </div>
    <Pc v-else-if="game.type <= 5" :game="game" @rule="showRule" />
    <Sc v-else-if="game.type <= 9" :game="game" @rule="showRule" />
    <Wu v-else-if="game.type <= 11" :game="game" @rule="showRule" />
    <Football v-else-if="game.type === 12" :game="game" />
</template>

<style scoped>
.carousel {
    text-align: center;
}

.carousel .image {
    width: calc(100vw - 36px);
    height: 170px;
}

#marquee {
    display: flex;
    height: 29px;
    background-color: #4f4f4f;
    line-height: 29px;
    font-size: 12.6px;
    color: #fff;
    padding: 0 7px;
}

#marquee .icon {
    margin: 4px 5px 0 0;
}

#marquee .icon img {
    display: block;
    width: 20px;
    height: 20px;
}

.marque-notice-bar {
    width: 100%;
    padding: 0;
    background: none;
    height: 29px;
}

.marque-notice-bar .van-notice-bar {
    padding: 0;
}

#games {
    padding-bottom: 6vw;
}

#games .game {
    display: inline-block;
    margin: 4px 0 0 6vw;
}

#games .game img {
    display: block;
    width: 25vw;
}

#notice-cover {
    position: absolute;
    left: 0;
    top: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.63);
    z-index: 98;
}

#notice {
    position: absolute;
    width: 279px;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
    border-radius: 14.4px;
    background-color: #fff;
    z-index: 99;
}

#notice .header {
    height: 82px;
    background-size: 100%;
    text-align: center;
    line-height: 82px;
    font-size: 24px;
    color: #fff;
}

#notice .content {
    padding: 14px 35px;
    color: #09172F;
    font-size: 14px;
    line-height: 34px;
}

#notice .button {
    width: 210px;
    height: 50px;
    background-image: linear-gradient(90deg, #EBCA93 2%, #DAB576 100%);
    border-radius: 25px;
    text-align: center;
    line-height: 50px;
    margin: 0 auto 23px auto;
    font-size: 16px;
    color: #09172F;
}
</style>