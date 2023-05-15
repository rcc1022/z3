<script>
import back from '@/assets/back.png';
export default {
    props: ['label'],
    emits: ['back'],
    data() {
        return {
            back: back
        };
    }
}
</script>
<template>
    <div class="app-bar">
        <div class="app-bar-icon" @click="$emit('back')"><img :src="back" /></div>
        <div class="app-bar-label">{{ label}}</div>
        <div class="app-bar-icon"></div>
    </div>
</template>
<style>
.app-bar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 44px;
}

.app-bar-icon {
    width: 23px;
    height: 23px;
}

.app-bar-icon img {
    width: 23px;
    height: 23px;
}

.app-bar-label {
    font-weight: 500;
    font-size: 18px;
    color: #fff;
    letter-spacing: 0;
    text-align: center;
}
</style>