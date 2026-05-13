import { useUserStore } from '@/store/modules/user'

const permissionDirective = {
  mounted(el, binding) {
    const { value } = binding
    if (value && Array.isArray(value) && value.length > 0) {
      const userStore = useUserStore()
      const permissions = userStore.permissions
      const hasPermission = value.some((perm) => permissions.includes(perm))
      if (!hasPermission) {
        el.parentNode?.removeChild(el)
      }
    } else if (typeof value === 'string') {
      const userStore = useUserStore()
      const permissions = userStore.permissions
      if (!permissions.includes(value)) {
        el.parentNode?.removeChild(el)
      }
    }
  }
}

export default permissionDirective
