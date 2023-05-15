<script>
export default {
    props: ['content'],
    emits: ['ok', 'cancel']
}
</script>
<template>
    <div class="alert-mark" @click="$emit('cancel')">
        <div class="alert" @click.stop="">
            <div class="alert-content">{{ content }}</div>
            <div class="alert-ok" @click="$emit('ok')">确认</div>
        </div>
    </div>
</template>
<style>
.alert-mark {
    position: absolute;
    left: 0;
    top: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.63);
    z-index: 99;
}

.alert {
    position: absolute;
    left: 50vw;
    top: 50vh;
    transform: translate(-50%, -50%);
    width: 80vw;
    border-radius: 4px;
    overflow: hidden;
    background-color: #fff;
    font-size: 12px;
}

.alert-content {
    text-align: center;
    padding: 16px 0 13px 0;
}

.alert-ok {
    background-color: rgb(242, 242, 242);
    border-top: 1px solid rgb(211, 211, 211);
    text-align: center;
    padding: 5px 0 8px 0;
    color: rgb(81, 183, 245);
}
</style>