<template>
  <div class="member-page">
    <div class="plans">
      <div
          v-for="plan in plans"
          :key="plan.name"
          class="plan"
          :class="{ highlight: selectedPlan === plan }"
          @click="selectPlan(plan)"
      >
        <div>{{ plan.name }}</div>
        <div class="price">¥{{ plan.price }}</div>
        <div>{{ plan.description }}</div>
      </div>
    </div>

    <div class="payment">
      <h3>确认支付</h3>
      <div class="payment-content">
        <div id="qr-code">
          <img :src="qrCodeUrl" alt="支付二维码">
        </div>
        <div class="payment-info">
          <div class="member-price">
            支付方式：微信支付/支付宝支付
          </div>
          <div class="member-price">
            支付金额：¥{{ selectedPlan ? selectedPlan.price : 0 }}
          </div>
        </div>
        <div class="button-group">
          <button class="payment-button" @click="handlePayment">已完成支付</button>
          <button class="payment-button" @click="backToHome">返回主页</button>
        </div>
      </div>
    </div>

    <div class="footer">
      © 2024 VIP订阅系统 | 提供安全便捷的VIP服务
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import '../assets/member.css'

export default {
  name: 'Member',

  data() {
    return {
      plans: [
        { name: "连续包月", price: 25, description: "仅25元/月" },
        { name: "连续包季", price: 50, description: "省18元" },
        { name: "连续包年", price: 178, description: "低至14.8元/月" },
        { name: "12个月VIP", price: 198, description: "16.5元/月" },
        { name: "3个月VIP", price: 60, description: "送10元券" }
      ],
      selectedPlan: null,
      qrCodeUrl: new URL('/images/QR_code.png', import.meta.url).href
    }
  },

  created() {
    this.selectDefaultPlan()
  },

  methods: {
    selectPlan(plan) {
      this.selectedPlan = plan
    },

    selectDefaultPlan() {
      const defaultPlan = this.plans.find(p => p.name === "连续包年")
      if (defaultPlan) {
        this.selectedPlan = defaultPlan
      }
    },

    async handlePayment() {
      try {
        // 获取 localStorage 中的用户ID
        const userId = localStorage.getItem('userId')

        // 发送 POST 请求到后端
        const response = await axios.post('http://localhost:8080/setmember', {}, {
          withCredentials: true  // 确保携带 Session Cookie
        })

        if (response.data === "成功成为会员") {
          localStorage.setItem('isMember', 'true')
          alert('成功成为会员！')
          this.$router.push('/profile')
        } else {
          alert('会员状态更新失败，请重试')
        }
      } catch (error) {
        console.error('Error:', error)
        alert('更新会员状态失败，请重试')
      }
    },

    backToHome() {
      this.$router.push('/profile')
    }
  }
}
</script>

<style>
.member-page {
  margin-top: 50px;
}
</style> 