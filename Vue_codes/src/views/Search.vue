<template>
  <div id="app" >
    <div class="page-container">
      <!-- 顶部导航栏 -->
      <header>
        <div class="header-container">
          <div class="logo">
            <h1>供货商智能搜索系统</h1>
          </div>
          <nav>
            <ul>
              <router-link :to="{path: '/'}" custom v-slot="{ navigate }">
                <li><a @click="navigate">首页</a></li>
              </router-link>
              <router-link v-slot="{ navigate }">
                <li><a @click="handleClicks('signin')">个人中心</a></li>
              </router-link>
              <li><a @click="navigate">帮助中心</a></li>
            </ul>
          </nav>
        </div>
      </header>

      <!-- 搜索区域 -->
      <div class="search-area">
        <h2>欢迎使用供货商智能搜索系统</h2>
        <div class="search-box" :class="{ expanded: isAdvanced }">
          <input
              type="text"
              id="search-bar"
              v-model="searchContent"
              :placeholder="placeholderText"
          />
          <button id="search-btn" @click="handleSearch">搜索</button>
        </div>
        <div class="search-links">
          <a href="#" @click.prevent="toggleSearch(false)">普通搜索</a>
          <a href="#" @click.prevent="toggleSearch(true)">高级搜索</a>
        </div>
      </div>

      <!-- 结果展示区域 -->
      <div class="results-container" v-if="searchResults.length > 0" id="test">
        <div class="filter-sidebar">
          <h3>供货商地区</h3>
          <ul>
            <li v-for="(count, location) in locationFilterCounts" :key="location">
              <input
                  type="checkbox"
                  :value="location"
                  v-model="selectedLocations"
                  @change="applyFilters"
              />
              {{ location }} ({{ count }})
            </li>
          </ul>
          <button class="filter-reset-btn" @click="resetFilters">重置筛选</button>
        </div>
        <div class="results-header">
          <h2>搜索结果</h2>
          <span>共找到 <span style="color: deepskyblue; margin: 0 auto;">{{ searchResults.length }}</span> 个结果</span>
        </div>
        <table class="results-table">
          <thead>
          <tr>
            <th>商品名称</th>
            <th>供货商</th>
            <th>发货地</th>
            <th>近日销量</th>
            <th>星级评分</th>
            <th>价格</th>
            <th>操作</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="(item, index) in paginatedResults" :key="index">
            <td>{{ item.ProductName }}</td>  <!-- 显示商品名称 -->
            <td>{{ item.StoreName }}</td>    <!-- 显示供货商 -->
            <td>{{ item.ShippingLocation }}</td> <!-- 显示地区 -->
            <td>{{ item.Turnover }}</td>    <!-- 显示销量 -->
            <td>{{ item.DescribeRating }}</td>  <!-- 显示评分 -->
            <td>{{ item.Price }}</td>      <!-- 显示价格 -->
            <td>

              <button class="details-btn" @click="goToDetail(item.LinkUrl)">查看详情</button>
              <button class="favorite-btn" @click="handleCollection(item)">收藏</button>
            </td>
          </tr>
          </tbody>
        </table>

        <!-- 分页组件 -->
        <Pagination
            :currentPage="currentPage"
            :totalPages="totalPages"
            @updatePage="updatePage"
        />
      </div>

      <!-- 页脚 -->
      <footer>
        <p>© 2024 智能供货商选择系统 - 提供精准的供货商搜索服务</p>
      </footer>

      <!-- 加载中弹窗 -->
      <div class="loading-modal" v-if="isLoading">
        <div class="loading-spinner"></div>
        <p>正在进行智能分析...</p>
        <p style="display: block;">大约需要十几秒，请耐心等待</p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import Pagination from '../components/Pagination.vue'; // 导入分页组件

export default {
  components: {
    Pagination
  },
  data() {
    return {
      searchContent: "",
      isAdvanced: false, // 标记是否为高级搜索模式
      searchResults: [], // 搜索结果
      selectedLocations: [],
      currentPage: 1, // 当前页码
      resultsPerPage: 10, // 每页显示的结果数
      isLoading: false, // 添加这个控制加载状态的变量
    };
  },
  computed: {
    normalizedResults() {
      return this.searchResults.map(item => {
        return {
          productName: item.ProductName, // 映射为规范的属性名
          storeName: item.StoreName,
          location: item.ShippingLocation,
          turnover: item.Turnover,
          rating: item.DescribeRating,
          price: item.Price,
          linkUrl: item.LinkUrl
        };
      });
    },
    locationFilterCounts() {
      const counts = {};
      this.searchResults.forEach((item) => {
        const location = item.ShippingLocation; // 使用正确的字段名
        counts[location] = (counts[location] || 0) + 1;
      });
      console.log("locationFilterCounts:", counts); // 调试：输出筛选条件计数
      return counts;
    },
    filteredResults() {
      let filtered = this.searchResults;
      if (this.selectedLocations.length > 0) {
        filtered = this.searchResults.filter((item) =>
            this.selectedLocations.includes(item.ShippingLocation) // 使用正确的字段名
        );
      }
      console.log("filteredResults:", filtered); // 调试：输出过滤后的结果
      return filtered;
    },
    sortedResults() {
      const sorted = [...this.filteredResults];
      if (this.selectedSort === "Price") {
        sorted.sort((a, b) => a.price - b.price); // 使用正确的字段名
      } else if (this.selectedSort === "Turnover") {
        sorted.sort((a, b) => b.turnover - a.turnover); // 使用正确的字段名
      }
      console.log("sortedResults:", sorted); // 调试：输出排序后的结果
      return sorted;
    },
    placeholderText() {
      return this.isAdvanced ? "请输入您要搜索的内容" : "请输入关键词...";
    },
    totalPages() {
      return Math.ceil(this.searchResults.length / this.resultsPerPage);
    },
    paginatedResults() {
      const start = (this.currentPage - 1) * this.resultsPerPage;
      const end = start + this.resultsPerPage;
      const paginated = this.sortedResults.slice(start, end);
      console.log("paginatedResults:", paginated);
      return paginated;
    },
  },
  name: 'App',
  mounted() {
    this.handleClick(); // 页面加载时自动调用
  },
  methods: {
    goToDetail(linkurl) {
      console.log('Received linkUrl:', linkurl); // 输出 linkUrl 值
      if (!linkurl || linkurl === 'undefined') {
        console.error('Invalid linkurl:', linkurl);
        alert('链接无效，无法跳转。');
        return; // 提示并返回
      }

      // 在新窗口中打开链接
      const newWindow = window.open(linkurl, '_blank');
      if (!newWindow) {
        console.error('新窗口未能打开，请检查浏览器设置');
        alert('无法在新窗口中打开链接');
      }
    },
    resetFilters() {
      this.selectedLocations = [];
      this.applyFilters();
      console.log("resetFilters - selectedLocations reset"); // 调试：输出重置筛选操作
    },
    handleClick() {
      // 调用后端接口检查是否登录
      axios.get('http://localhost:8080/check-session', { withCredentials: true })
          .then(response => {
            const user = response.data.user; // 假设后端返回的用户信息存在 response.data.user

            if (user) {

            } else {
              // 如果用户未登录，跳转到后端的 /signin 页面
              window.location.href = 'http://localhost:8080/signin';  // 后端登录页面
            }
          })
          .catch(error => {
            console.error('获取用户信息失败:', error);
            // 如果获取用户信息失败，跳转到后端登录页面
            window.location.href = 'http://localhost:8080/signin';
          });
    },
    handleClicks(){
      // 调用后端接口检查是否登录
      axios.get('http://localhost:8080/check-session', { withCredentials: true })
          .then(response => {
            const user = response.data.user; // 假设后端返回的用户信息存在 response.data.user

            if (user) {
              this.$router.push('/profile');
            } else {
              // 如果用户未登录，跳转到后端的 /signin 页面
              window.location.href = 'http://localhost:8080/signin';  // 后端登录页面
            }
          })
          .catch(error => {
            console.error('获取用户信息失败:', error);
            // 如果获取用户信息失败，跳转到后端登录页面
            window.location.href = 'http://localhost:8080/signin';
          });
    },
    handleSearch() {
      if (!this.searchContent.trim()) {
        alert("请输入搜索内容！");
        return;
      }

      if (this.isAdvanced) {
        console.log("高级搜索:", this.searchContent);
        // 触发高级搜索事件（示例接口）
        this.searchAdvance(this.searchContent)
      } else {
        console.log("普通搜索:", this.searchContent);
        // 触发普通搜索事件（发送请求到后端）
        this.searchNormal(this.searchContent);
      }
    },
    toggleSearch(isAdvanced) {
      this.isAdvanced = isAdvanced;
      this.searchContent = ""; // 切换模式时清空输入框
      this.searchResults = [];
    },
    handleCollection(item) {
      const productid = item.ProductId;
      // 发送请求
      axios.get('http://localhost:8080/setcollection', {
        params: {
          productid: productid
        },
        withCredentials: true,
        paramsSerializer: params => {
          return Object.entries(params)
              .map(([key, value]) =>
                  `${encodeURIComponent(key)}=${encodeURIComponent(value)}`)
              .join('&');
        }
      })
          .then(response => {
            if (response.data === "收藏成功") {
              alert("收藏成功！");
            } else if (response.data === "该记录已存在") {
              alert("该商品已在收藏列表中！");
            }
          })
          .catch(error => {
            console.error('收藏失败:', error);
            console.log('请求参数:', shortProductName);
            if (error.response) {
              console.log('错误响应:', error.response);
              alert("收藏失败：" + (error.response.data || "请稍后重试"));
            } else if (error.request) {
              alert("网络连接失败，请检查网络后重试");
            } else {
              alert("收藏失败，请稍后重试");
            }
          })
    },
    // 普通搜索方法
    searchNormal(content) {
      axios
          .get(`/search/result_main?content=${content}`)
          .then((response) => {
            console.log("搜索结果:", response.data);
            this.searchResults = response.data; // 更新搜索结果
            this.currentPage = 1; // 每次搜索清除当前页
          })
          .catch((error) => {
            console.error("搜索失败:", error);
          });
    },
    // AI搜索方法
    searchAdvance(content) {
      this.isLoading = true; // 开始加载时显示弹窗
      axios.get(`http://localhost:5000/search/result_advance?content=${content}`, {
        withCredentials: true,  // 让请求携带当前 session 的 cookie
      })
          .then((response) => {
            console.log("高级搜索结果:", response.data);
            this.searchResults = response.data.recommended_products; // 更新搜索结果
            console.log("单个搜索结果:", this.searchResults[0]);
            this.currentPage = 1; // 每次搜索清除当前页
          })
          .catch((error) => {
            console.error("搜索失败:", error);
          })
          .finally(() => {
            this.isLoading = false; // 无论成功失败都关闭弹窗
          });
    },
    updatePage(page) {
      this.currentPage = page;
    },
  },
};
</script>

<style scoped>
/* 全局样式 */
body {
  font-family: Arial, sans-serif;
  margin: 0;
  padding: 0;
  background-color: #f9f9f9;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.page-container {
  flex: 1;
}

/* 顶部导航栏 */
.header-container {
  background-color: #005bac;
  color: white;
  padding: 10px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo h1 {
  margin: 0;
  font-size: 24px;
}

nav ul {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  gap: 20px;
}

nav ul li a {
  color: white;
  text-decoration: none;
  font-size: 14px;
}

nav ul li a:hover {
  text-decoration: underline;
}

/* 搜索区域 */
.search-area {
  text-align: center;
  padding: 40px 20px;
  background-color: #eaf4fc;
}

.search-box {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  transition: all 0.3s ease;
  width: 60%;
  margin: 0 auto;
}

.search-box.expanded {
  flex-direction: column;
}

.search-box.expanded #search-bar {
  height: 150px;
  resize: none;
  width: 100%;
}

#search-bar {
  padding: 10px;
  font-size: 16px;
  border: 1px solid #ccc;
  border-radius: 4px;
  width: 500px;
}

.search-links {
  margin-top: 10px;
}

.search-links a {
  text-decoration: none;
  color: #005bac;
  margin: 0 10px;
  font-size: 14px;
}

.search-links a:hover {
  text-decoration: underline;
}

/* 按钮样式 */
button {
  padding: 10px 20px;
  font-size: 16px;
  border: none;
  border-radius: 4px;
  color: white;
  cursor: pointer;
}

button:hover {
  opacity: 0.9;
}

.search-btn {
  background-color: #005bac;
}

.reset-btn {
  background-color: #ff5722;
}

.pagination button {
  background-color: #005bac;
  margin-bottom: 20px;
}

.pagination button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

/* 表格样式 */
.results-table {
  width: 100%;
  height: 60%;
  border-collapse: collapse;
  margin-bottom: 20px;
}

.results-table th,
.results-table td {
  border: 1px solid #ddd;
  padding: 12px;
  text-align: center;
}

.results-table th {
  background-color: #f3f6f8;
  color: #333;
}

.results-table tr:nth-child(even) {
  background-color: #f9f9f9;
}

/* 页脚 */
footer {
  background-color: #333;
  color: white;
  text-align: center;
  padding: 10px 0;
  font-size: 14px;
  margin-top: auto;
}
/* 搜索区域样式 */
.search-area {
  text-align: center;
  padding: 40px 20px;
  background-color: #eaf4fc;
}

.search-box {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  transition: all 0.3s ease;
  position: relative;
  margin: 0 auto;
  width: 60%;
}

.search-box.expanded {
  flex-direction: column;
}

.search-box.expanded #search-bar {
  height: 150px;
  resize: none;
  width: 100%;
  padding-top: 30px;
}

#search-bar {
  padding: 10px;
  font-size: 16px;
  border: 1px solid #ccc;
  border-radius: 4px;
  width: 500px;
}

#search-btn {
  background-color: #005bac;
  color: white;
  border: none;
  border-radius: 4px;
  padding: 10px 15px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

#search-btn:hover {
  background-color: #003f7d;
}

.search-links {
  margin-top: 10px;
}

.search-links a {
  text-decoration: none;
  color: #005bac;
  margin: 0 10px;
  font-size: 14px;
}

.search-links a:hover {
  text-decoration: underline;
}

.results-header h2{
  height: 40px;
  border-bottom: 5px solid #005bac;
  margin: 10px;
  border-radius: 2px;
}

.results-header span{
  margin-left: 20px;

}

/* 搜索结果表格样式 */
.results-container {
  margin-top: 40px;
}

.results-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}

.results-table th,
.results-table td {
  padding: 12px;
  text-align: left;
  border: 1px solid #ddd;
}

.results-table th {
  background-color: #f4f4f4;
}

.details-btn,
.add-cart-btn,
.favorite-btn {
  padding: 5px 10px;
  margin: 5px;
  cursor: pointer;
}

.details-btn {
  background-color: #4caf50;
  color: white;
}

.add-cart-btn {
  background-color: #2196f3;
  color: white;
}

.favorite-btn {
  background-color: #ff9800;
  color: white;
}

.details-btn:hover,
.add-cart-btn:hover,
.favorite-btn:hover {
  opacity: 0.8;
}
.filter-reset-btn {
  background-color: #005bac;
  color: white;
  margin-left: 20px;
}
.filter-sidebar h3{
  margin-left: 20px;
}
.filter-sidebar ul{
  margin-left: 20px;
  padding: 0;
  display: flex;  /* 使用 flex 布局 */
  flex-wrap: wrap;  /* 允许换行 */
  gap: 15px;  /* 选项之间的间距 */
  list-style: none;  /* 移除默认的列表样式 */
}

.filter-sidebar li {
  display: flex;
  align-items: center;
  gap: 5px;  /* 复选框和文字之间的间距 */
  min-width: 100px;  /* 每个选项的最小宽度 */
}

.filter-sidebar input[type="checkbox"] {
  margin: 0;  /* 移除复选框默认边距 */
}

.pagination{
  margin-bottom: 40px;
}
#app {
  width: 100%; /* 设置宽度为 100% */
  height: 100%; /* 设置高度为 100% */
}

/* 加载中弹窗样式 */
.loading-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 5px solid #f3f3f3;
  border-top: 5px solid #005bac;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

.loading-modal p {
  color: white;
  font-size: 18px;
  margin: 0;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

</style>
