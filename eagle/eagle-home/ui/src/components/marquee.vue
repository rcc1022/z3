<script>
export default {
}
</script>
<template>
    <van-notice-bar class="marque-notice-bar" :text="message" />
</template>
<style>
.marque-notice-bar {
    width: 100%;
    padding: 0;
    background: none;
}

.marque-notice-bar .van-notice-bar {
    padding: 0;
    height: 100%;
    color: none;
}

.marquee {
    width: 100%;
    overflow: hidden;
    position: relative;
    white-space: nowrap;
}

.marquee div {
    animation: marquee 5s linear infinite;
}

@keyframes marquee {
    0% {
        margin-left: 100%;
    }

    100% {
        margin-left: -100%;
    }
}
</style>