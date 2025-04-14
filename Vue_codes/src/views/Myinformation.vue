<template>
  <div id="app">
    <header>
      <div class="inner">
        <div class="header-right fr">
          <div class="search">
            <router-link :to="{path: '/'}" custom v-slot="{ navigate }">
              <button class="search-btn" @click="navigate" style="left: 100px">回到首页</button>
            </router-link>
            <router-link :to="{path: '/member'}" custom v-slot="{ navigate }">
              <button class="search-btn" @click="navigate">成为会员</button>
            </router-link>
          </div>
        </div>
      </div>
    </header>
    <div class="cl"></div>
    <div class="content">
      <div class="inner" style="width: 1100px;display: flex;">
        <div id="left" style="width: 240px;height: 1135px;">
          <div class="content-left fl">
            <nav>
              <ul>
                <li class="active">
                  <a href="#">我的主页</a>
                </li>
                <li>
                  <a href="#browsing-history">浏览历史</a>
                </li>
                <li>
                  <a href="#my-favorites">我的收藏</a>
                </li>
                <li>
                  <a href="#edit-profile">编辑个人资料</a>
                </li>
                <li>
                  <a href="#become-member">成为会员</a>
                </li>
              </ul>
            </nav>
          </div>
        </div>

        <div class="content-right fl">
          <div class="right-top">
            <div class="box">
              <div class="top">
                <div class="top-left">
                  <div class="user-pic">
                    <img src="/images/avatars/1733455945341_OIP.jpg" alt="">
                  </div>
                  <div class="user-info">
                    <div class="user-name">{{ username }}</div>
                    <div class="account">
                      我的账号 {{ username }}
                      <a href="#" class="address" @click="editAddress">收货地址: {{address}}}</a>
                    </div>
                  </div>
                </div>
                <div class="top-right">
                  <div id="membership-status">
                    <img v-if="isMember" :src="memberImage" alt="会员专属图片" style="width: 80px; height: auto;">
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="right-mid">
            <div class="box" id="browsing-history">
              <div class="title">浏览历史</div>
              <div class="history">
                <ul>
                  <li>
                    <div class="history-img">
                      <img src="/images/img1.jpg" alt="">
                    </div>
                    <div class="txts">
                      <p>直液式速干走珠中性笔 0.5针管式速干水笔碳素笔学生考试签字黑笔</p>
                    </div>
                    <div class="price-box">
                      <div class="price1">￥</div>
                      <div class="price-num">2.01</div>
                      <div class="tag">热销商品</div>
                    </div>
                  </li>
                  <li>
                    <div class="history-img">
                      <img src="/images/img2.jpg" alt="">
                    </div>
                    <div class="txts">
                      <p>【直充】bilibili大会员哔哩哔哩vip周卡30天一个月季年卡B站会员</p>
                    </div>
                    <div class="price-box">
                      <div class="price1">￥</div>
                      <div class="price-num">2.01</div>
                      <div class="tag">热销商品</div>
                    </div>
                  </li>
                  <li>
                    <div class="history-img">
                      <img src="/images/img1.jpg" alt="">
                    </div>
                    <div class="txts">
                      <p>直液式速干走珠中性笔 0.5针管式速干水笔碳素笔学生考试签字黑笔</p>
                    </div>
                    <div class="price-box">
                      <div class="price1">￥</div>
                      <div class="price-num">2.01</div>
                      <div class="tag">热销商品</div>
                    </div>
                  </li>
                </ul>
              </div>
            </div>
            <div class="box" id="my-favorites">
              <div class="title">我的收藏</div>
              <table class="results-table">
                <thead>
                <tr>
                  <th>商品名</th>
                  <th>卖家店铺名</th>
                  <th>发货地</th>
                  <th>近期购买数量</th>
                  <th>价格</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="favorite in favorites" :key="favorite.name">
                  <td>{{ favorite.productName }}</td>
                  <td>{{ favorite.storeName }}</td>
                  <td>{{ favorite.shippingLocation }}</td>
                  <td>{{ favorite.turnover }}</td>
                  <td>¥{{ favorite.price.toFixed(2) }}</td>
                </tr>
                </tbody>
              </table>
            </div>
          </div>
          <div class="box" id="edit-profile">
            <div class="title">编辑个人资料</div>
            <div id="change">
              <div id="modify_name" class="modify" @click="editName">
                <div class="tubiao"><img src="/images/modify_name.svg"></div>
                <p>修改用户名</p>
              </div>
              <div id="modify_image" class="modify" @click="triggerFileInput">
                <div class="tubiao"><img src="/images/modify_image.svg"></div>
                <p>修改头像</p>
                <input type="file" accept="image/*" @change="uploadAvatar" style="display: none" ref="fileInput" />
              </div>
              <div id="modify_email" class="modify" @click="editEmail">
                <div class="tubiao"><img src="/images/modify_email.svg"></div>
                <p>修改邮箱</p>
              </div>
              <div id="modify_address" class="modify" @click="editAddress">
                <div class="tubiao"><img src="/images/modify_address.svg"></div>
                <p>修改收货地址</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 编辑简介对话框 -->
    <div id="edit-name" class="modal" v-if="isEditNameModalOpen" @click.self="closeModal">
      <div class="modal-content">
        <span class="close" @click="closeModal">&times;</span>
        <h3>修改用户名</h3>
        <input type="text" id="username" name="username" placeholder="请输入新用户名..." v-model="username" required>
        <button type="button" class="save" @click="saveBio">保存</button>
      </div>
    </div>

    <div id="edit-email" class="modal" v-if="isEditEmailModalOpen" @click.self="closeModal">
      <div class="modal-content">
        <span class="close" @click="closeModal">&times;</span>
        <h3>修改邮箱</h3>
        <input type="text" id="email" name="email" placeholder="请输入新邮箱..." v-model="email" required>
        <button type="button" class="save" @click="saveBio">保存</button>
      </div>
    </div>

    <div id="edit-address" class="modal" v-if="isEditAddressModalOpen" @click.self="closeModal">
      <div class="modal-content">
        <span class="close" @click="closeModal">&times;</span>
        <h3>修改收货地址</h3>
        <input type="text" id="Address" name="Address" placeholder="请输入新地址..." v-model="address" required>
        <button type="button" class="save" @click="saveBio">保存</button>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import '../assets/style.css'

export default {
  name: 'UserProfile',
  data() {
    return {
      username: '',
      userAvatar: '/images/avatar/1733455945341_OIP.jpg', // 默认头像
      isMember: false,
      favorites: [],
      browsingHistory: [],
      isEditNameModalOpen: false,
      isEditEmailModalOpen: false,
      isEditAddressModalOpen: false,
      email: '',
      address: '',
      memberImage:'/images/member.svg'
    };
  },
  mounted() {
    this.getUserInfo();  // 获取用户信息
    this.getFavorites();
    this.checkMembership();
  },
  methods: {
    editName() {
      this.isEditNameModalOpen = true;
    },
    editEmail() {
      this.isEditEmailModalOpen = true;
    },
    editAddress() {
      this.isEditAddressModalOpen = true;
    },
    closeModal() {
      this.isEditNameModalOpen = false;
      this.isEditEmailModalOpen = false;
      this.isEditAddressModalOpen = false;
    },
    saveBio() {
      // 保存息的逻辑
    },
    getUserInfo() {
      axios.get("http://localhost:8080/getUserInfo", { withCredentials: true })
          .then(response => {
            const userInfo = response.data;
            this.username = userInfo.username;
            this.email = userInfo.email;
            this.address = userInfo.address;
            // 你可以根据用户信息设置头像等其他数据
            this.userAvatar = userInfo.avatar || this.userAvatar; // 假设后端返回了头像的 URL
          })
          .catch(error => {
            console.error('获取用户信息失败:', error);
          });
    },
    getFavorites() {
      axios.get("http://localhost:8080/getcollections",{ withCredentials: true })
          .then(response => {
            this.favorites = response.data;
          })
          .catch(error => {
            console.error('获取收藏数据失败:', error);
          });
    },
    async checkMembership() {
      try {
        // 如果需要使用用户邮箱作为请求的一部分，确保在请求中使用它
        const userEmail = localStorage.getItem('userId'); // 假设这里存储的是用户的邮箱
        // 发送 POST 请求
        const response = await axios.post('http://localhost:8080/ismember', {
          // 如果后端需要用户邮箱作为参数，可以在这里添加
          // email: userEmail
        }, {
          withCredentials: true  // 确保携带 Session Cookie
        });
        // 处理响应数据
        this.isMember = response.data;
      } catch (error) {
        // 处理错误
        console.error('检查会员状态失败:', error);
      }
    },
    triggerFileInput() {
      this.$refs.fileInput.click();
    },
    uploadAvatar(event) {
      const file = event.target.files[0];
      if (file) {
        const formData = new FormData();
        formData.append('avatar', file);

        axios.post('/api/uploadAvatar', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })
            .then(response => {
              if (response.data.success) {
                alert('头像上传成功');
              } else {
                alert('头像上传失败');
              }
            })
            .catch(error => {
              console.error('Error:', error);
              alert('头像上传失败');
            });
      }
    }
  }
};
</script>

<style scoped>

.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}
.modal-content {
  background-color: white;
  padding: 30px;
  border-radius: 8px;
  width: 500px;
  position: relative;
}
.modal-content h3 {
  margin-bottom: 20px;
  font-size: 20px;
  text-align: center;
}
.modal-content input {
  width: 100%;
  padding: 12px;
  margin: 10px 0;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}
.modal-content .save {
  width: 100%;
  padding: 12px;
  background-color: #005bac;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  margin-top: 20px;
}
.close {
  position: absolute;
  right: 20px;
  top: 15px;
  font-size: 24px;
  cursor: pointer;
  color: #666;
}
.close:hover {
  color: #000;
}

</style>