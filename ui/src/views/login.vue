<template>
  <div>
    <div class="login_module">
      <div class="login_bg">
        <!-- <img src="@/assets/images/login-background.png" alt=""> -->
        <!-- <div class="login_bg_span">
          <div class="login_bg_title">
            聚合聊天系统
          </div>
          <div class="login_bg_desc">
            连接所有社交平台，让沟通更简单
          </div>
        </div> -->
      </div>

      <div class="login_main">
        <div class="logo">
          <img src="@/assets/logo/logo.jpg" alt="">
          <div>{{ $t('login.welcome') }}</div>
        </div>

        <div class="login_form">
          <el-form ref="loginForm" :model="loginForm" :rules="loginRules">
            <el-form-item prop="username">
              <el-input prefix-icon="el-icon-user-solid" v-model="loginForm.username" type="text" auto-complete="off"
                :placeholder="$t('login.username')"></el-input>
            </el-form-item>
            <el-form-item prop="password">
              <el-input prefix-icon="el-icon-lock" v-model="loginForm.password" type="password" auto-complete="off"
                :placeholder="$t('login.password')" @keyup.enter.native="handleLogin"></el-input>
            </el-form-item>
            <el-form-item prop="code" v-if="captchaEnabled">
              <el-input v-model="loginForm.code" auto-complete="off" :placeholder="$t('login.code')" style="width: 63%"
                @keyup.enter.native="handleLogin">
                <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
              </el-input>
              <div class="login-code">
                <img :src="codeUrl" @click="getCode" class="login-code-img" />
              </div>
            </el-form-item>
            <el-form-item>
              <!-- 记住密码勾选 -->
              <div style="display: flex;align-items: center;justify-content: space-between;">
                <el-checkbox v-model="loginForm.rememberMe">{{ $t('login.rember') }}</el-checkbox>

                <el-dropdown style="cursor: pointer;" trigger="click" @command="handleLanguageChange">
                  <div class="language-menu">
                    <i class="el-icon-message" />
                    <span style="margin-left: 5px; font-size: 14px;">{{ $i18n.locale === 'zh' ? '中文' : 'English'
                      }}</span>
                  </div>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item command="zh" :class="{ 'selected-language': $i18n.locale === 'zh' }">
                      <span>中文</span>
                    </el-dropdown-item>
                    <el-dropdown-item command="en" :class="{ 'selected-language': $i18n.locale === 'en' }">
                      <span>English</span>
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </div>
            </el-form-item>
            <el-form-item>
              <el-button :loading="loading" style="width: 100%;" type="primary" @click.native.prevent="handleLogin">
                <span v-if="!loading">{{ $t('login.login') }}</span>
                <span v-else>{{ $t('login.loginLoading') }}</span>
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { getCodeImg } from "@/api/login";
import Cookies from "js-cookie";
import { encrypt, decrypt } from '@/utils/jsencrypt'

export default {
  name: "Login",
  data() {
    return {
      codeUrl: "",
      loginForm: {
        username: "",
        password: "",
        rememberMe: false,
        code: "",
        uuid: ""
      },
      loginRules: {
        username: [
          { required: true, trigger: "blur", message: this.$t('login.username') }
        ],
        password: [
          { required: true, trigger: "blur", message: this.$t('login.password') }
        ],
        code: [{ required: true, trigger: "change", message: this.$t('login.code') }]
      },
      loading: false,
      // 验证码开关
      captchaEnabled: false,
      // 注册开关
      register: false,
      redirect: undefined
    };
  },
  watch: {
    $route: {
      handler: function (route) {
        this.redirect = route.query && route.query.redirect;
      },
      immediate: true
    }
  },
  created() {
    this.getCode();
    this.getCookie();
  },
  methods: {
    handleLanguageChange(lang) {
      this.$i18n.locale = lang
      Cookies.set('language', lang)
      this.$message({
        message: lang === 'zh' ? '切换语言成功' : 'Language changed successfully',
        type: 'success'
      })
    },
    getCode() {
      getCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled;
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img;
          this.loginForm.uuid = res.uuid;
        }
      });
    },
    getCookie() {
      const username = Cookies.get("username");
      const password = Cookies.get("password");
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
      };
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true;
          if (this.loginForm.rememberMe) {
            Cookies.set("username", this.loginForm.username, { expires: 30 });
            Cookies.set("password", encrypt(this.loginForm.password), { expires: 30 });
            Cookies.set('rememberMe', this.loginForm.rememberMe, { expires: 30 });
          } else {
            Cookies.remove("username");
            Cookies.remove("password");
            Cookies.remove('rememberMe');
          }
          this.$store.dispatch("Login", this.loginForm).then(() => {
            this.loading = false;
            this.$router.push({ path: this.redirect || "/" }).catch(() => { });
          }).catch(() => {
            this.loading = false;
            if (this.captchaEnabled) {
              this.getCode();
            }
          });
        }
      });
    }
  }
};
</script>
<style lang="scss" scoped>
.login_module {
  display: flex;
  height: 100vh;
  background-color: #F9FAFB;

  .login_bg {
    flex: 1;
    position: relative;
    background: url("/img/login-background.png");
    background-position: center;
    background-repeat: no-repeat;
    background-size: cover;

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100vh;
      background: rgba(0, 0, 0, 0.2);
      z-index: 0;
    }

    .login_bg_span {
      position: absolute;
      top: 50%;
      left: 10%;
      transform: translateY(-50%);
      text-align: left;
      color: #fff;

      .login_bg_title {
        font-size: 36px;
        font-weight: bold;
        margin-bottom: 20px;
      }

      .login_bg_desc {
        font-size: 16px;
      }
    }
  }

  .login_main {
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
    height: 100vh;
    overflow-y: auto;

    .logo {
      text-align: center;
      margin-bottom: 30px;

      img {
        width: 80px;
        margin-bottom: 10px;
        border-radius: 50%;
      }

      div {
        font-size: 24px;
        font-weight: bold;
        color: #1F2937;
      }
    }

    .login_form {
      width: 300px;

      .el-form-item {
        margin-bottom: 20px;
      }

      ::v-deep .el-input {
        .el-input__icon {
          line-height: 45px !important;
        }

        .el-input__inner {
          height: 45px !important;
          border: none;
          box-shadow: 0px 1.5px 1px rgba(68, 68, 68, 0.1);
          border-radius: 5px;
        }
      }

      .el-button {
        height: 40px;
      }

      .el-checkbox {
        color: #1F2937;
      }
    }
  }
}
</style>
