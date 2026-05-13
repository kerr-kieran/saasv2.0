<template>
  <footer class="site-footer">
    <div class="footer-inner">
      <div class="footer-links">
        <a href="#">关于我们</a>
        <a href="#">联系我们</a>
        <a href="#">帮助中心</a>
        <a href="#">隐私政策</a>
        <a href="#">服务条款</a>
      </div>
      <p class="copyright">
        &copy; {{ year }} SaaS Mall. All rights reserved.
      </p>
    </div>
  </footer>
</template>

<script setup>
const year = new Date().getFullYear()
</script>

<style scoped>
.site-footer {
  background: #f5f5f5;
  border-top: 1px solid #e8e8e8;
  margin-top: 60px;
  padding: 30px 0;
}

.footer-inner {
  max-width: 1200px;
  margin: 0 auto;
  text-align: center;
  padding: 0 20px;
}

.footer-links {
  display: flex;
  justify-content: center;
  gap: 24px;
  margin-bottom: 12px;
}

.footer-links a {
  color: #666;
  text-decoration: none;
  font-size: 13px;
}

.footer-links a:hover {
  color: #e4393c;
}

.copyright {
  color: #999;
  font-size: 12px;
}
</style>
