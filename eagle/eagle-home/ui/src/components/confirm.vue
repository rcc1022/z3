<script>
export default {
    props: ['content'],
    emits: ['ok', 'cancel']
}
</script>
<template>
    <div class="confirm-mark" @click="$emit('cancel')">
        <div class="confirm" @click.stop="">
            <div class="confirm-content">{{ content }}</div>
            <div class="confirm-toolbar">
                <div class="confirm-cancel" @click="$emit('cancel')">取消</div>
                <div class="confirm-ok" @click="$emit('ok')">确认</div>
            </div>
        </div>
    </div>
</template>
<style>
.confirm-mark {
    position: absolute;
    left: 0;
    top: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.63);
    z-index: 99;
}

.confirm {
    position: absolute;
    left: 50vw;
    top: 50vh;
    transform: translate(-50%, -50%);
    width: calc(80vw + 1px);
    border-radius: 4px;
    overflow: hidden;
    background-color: #fff;
    font-size: 12px;
}

.confirm-content {
    text-align: center;
    padding: 16px 0 13px 0;
}

.confirm-toolbar {
    background-color: rgb(242, 242, 242);
    border-top: 1px solid rgb(211, 211, 211);
}

.confirm-toolbar div {
    display: inline-block;
    width: 40vw;
    text-align: center;
    padding: 8px 0 5px 0;
}

.confirm-ok {
    border-left: 1px solid rgb(211, 211, 211);
    color: rgb(81, 183, 245);
}
</style>