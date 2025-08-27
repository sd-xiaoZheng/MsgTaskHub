import Vue from 'vue'
import VueI18n from 'vue-i18n'
import Cookies from 'js-cookie'
import elementEnLocale from 'element-ui/lib/locale/lang/en'
import elementZhLocale from 'element-ui/lib/locale/lang/zh-CN'
import enLocale from './en'
import zhLocale from './zh'
import materialZh from './zh/material'
import materialEn from './en/material'
import msgZh from './zh/message'
import msgEn from './en/message'
import menusEn from './en/menus'
import menusZh from './zh/menus'
Vue.use(VueI18n)

const messages = {
  en: {
    ...enLocale,
    ...elementEnLocale,
    ...materialEn,
    ...msgEn,
    ...menusEn
  },
  zh: {
    ...zhLocale,
    ...elementZhLocale,
    ...materialZh,
    ...msgZh,
    ...menusZh
  }
}

export function getLanguage() {
  const chooseLanguage = Cookies.get('language')
  if (chooseLanguage) return chooseLanguage
  return 'zh'
}

const i18n = new VueI18n({
  locale: getLanguage(),
  messages
})

export default i18n