<template>
    <div class="phone-layout" v-show="visible">
        <div class="style-selector">
            <el-radio-group v-model="selectedStyle" @change="handleStyleChange">
                <el-radio-button label="default">默认样式</el-radio-button>
                <el-radio-button label="ios">iOS样式</el-radio-button>
                <el-radio-button label="simple">简约样式</el-radio-button>
            </el-radio-group>
        </div>

        <!-- 默认手机样式 -->
        <Phone v-if="selectedStyle === 'default'" :visible="visible" :messages="messages" :contact="contact"
            @close="$emit('close')" />

        <!-- iOS手机样式 -->
        <IosPhone v-if="selectedStyle === 'ios'" :visible="visible" :messages="messages" :contact="contact"
            @close="$emit('close')" />

        <!-- 简约手机样式 -->
        <SimplePhone v-if="selectedStyle === 'simple'" :visible="visible" :messages="messages" :contact="contact"
            @close="$emit('close')" />
    </div>
</template>

<script>
import Phone from './Phone.vue'
import IosPhone from './IosPhone.vue'
import SimplePhone from './SimplePhone.vue'

export default {
    name: 'PhoneLayout',
    components: {
        Phone,
        IosPhone,
        SimplePhone
    },
    props: {
        visible: {
            type: Boolean,
            default: false
        },
        messages: {
            type: Array,
            default: () => []
        },
        contact: {
            type: Object,
            default: () => ({})
        }
    },
    data() {
        return {
            selectedStyle: 'default'
        }
    },
    methods: {
        handleStyleChange(value) {
            this.selectedStyle = value
        }
    }
}
</script>

<style lang="scss" scoped>
.phone-layout {
    .style-selector {
        position: fixed;
        top: 20px;
        left: 50%;
        transform: translateX(-50%);
        z-index: 10000;
        background-color: rgba(255, 255, 255, 0.9);
        padding: 10px;
        border-radius: 8px;
        box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

        .el-radio-button__inner {
            padding: 8px 20px;
        }
    }
}
</style>