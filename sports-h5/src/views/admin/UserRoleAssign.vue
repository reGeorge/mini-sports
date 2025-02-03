<template>
  <div class="user-role-assign">
    <van-nav-bar
      title="用户角色分配"
      left-text="返回"
      left-arrow
      @click-left="onClickLeft"
    />
    
    <!-- 用户搜索 -->
    <div class="search-box">
      <van-search
        v-model="searchValue"
        placeholder="请输入用户昵称搜索"
        @search="onSearch"
        shape="round"
        show-action
        @cancel="onCancel"
      />
    </div>

    <!-- 用户列表 -->
    <div class="user-list" v-if="users.length > 0">
      <van-cell-group>
        <van-cell v-for="user in users" :key="user.id">
          <template #title>
            <div class="user-info">
              <span class="nickname">{{ user.nickname }}</span>
              <span class="phone">{{ user.phone }}</span>
            </div>
          </template>
          <template #label>
            <div class="current-roles">
              当前角色：
              <van-tag 
                v-for="role in user.roles" 
                :key="role.id" 
                :type="getRoleTagType(role.code)"
                class="role-tag"
                size="medium"
              >
                {{ role.name }}
              </van-tag>
            </div>
          </template>
          <template #right-icon>
            <van-button 
              size="small" 
              type="primary" 
              @click="showRoleSelect(user)"
            >
              分配角色
            </van-button>
          </template>
        </van-cell>
      </van-cell-group>
    </div>

    <!-- 无搜索结果提示 -->
    <div class="no-result" v-else-if="hasSearched">
      <van-empty description="未找到相关用户" />
    </div>

    <!-- 角色选择弹窗 -->
    <van-popup
      v-model:show="showRoleDialog"
      position="bottom"
      round
      :style="{ height: '60%' }"
    >
      <div class="role-select-dialog">
        <div class="dialog-header">
          <span class="title">选择角色</span>
          <van-icon name="cross" @click="showRoleDialog = false" />
        </div>
        <div class="dialog-content">
          <van-checkbox-group v-model="selectedRoles">
            <div class="role-list">
              <div v-for="role in allRoles" :key="role.id" class="role-item">
                <van-checkbox 
                  :name="Number(role.id)"
                >
                  <div class="role-info">
                    <span class="role-name">{{ role.name }}</span>
                    <span class="role-desc">{{ role.description }}</span>
                  </div>
                </van-checkbox>
              </div>
            </div>
          </van-checkbox-group>
        </div>
        <div class="dialog-footer">
          <van-button type="primary" block @click="saveUserRoles">保存</van-button>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { getRoles } from '@/api/role'
import { searchUsers, getUserRoles, assignUserRoles } from '@/api/user'

const router = useRouter()
const users = ref([])
const allRoles = ref([])
const searchValue = ref('')
const hasSearched = ref(false)
const showRoleDialog = ref(false)
const selectedRoles = ref([])
const currentUser = ref(null)

// 加载所有角色
const loadRoles = async () => {
  try {
    const res = await getRoles()
    allRoles.value = res.data
  } catch (error) {
    showToast('获取角色列表失败')
  }
}

// 搜索用户
const onSearch = async () => {
  if (!searchValue.value.trim()) {
    showToast('请输入搜索内容')
    return
  }
  try {
    const res = await searchUsers(searchValue.value)
    users.value = res.data
    hasSearched.value = true
    // 获取每个用户的角色
    for (const user of users.value) {
      const rolesRes = await getUserRoles(user.id)
      user.roles = rolesRes.data
    }
  } catch (error) {
    showToast('搜索失败')
  }
}

// 取消搜索
const onCancel = () => {
  searchValue.value = ''
  users.value = []
  hasSearched.value = false
}

// 显示角色选择
const showRoleSelect = async (user) => {
  currentUser.value = user
  // 确保角色ID是数字类型
  selectedRoles.value = user.roles.map(role => Number(role.id))
  showRoleDialog.value = true
}

// 切换角色选择
const toggleRole = (roleId) => {
  // 确保roleId是数字类型
  const numericRoleId = Number(roleId)
  const index = selectedRoles.value.indexOf(numericRoleId)
  if (index > -1) {
    selectedRoles.value.splice(index, 1)
  } else {
    selectedRoles.value.push(numericRoleId)
  }
}

// 保存用户角色
const saveUserRoles = async () => {
  try {
    // 确保发送给后端的是数字类型的ID数组
    const roleIds = selectedRoles.value.map(Number)
    await assignUserRoles(currentUser.value.id, roleIds)
    showToast('保存成功')
    showRoleDialog.value = false
    // 更新用户的角色显示
    const updatedRoles = allRoles.value.filter(role => 
      selectedRoles.value.includes(Number(role.id))
    )
    const userIndex = users.value.findIndex(u => u.id === currentUser.value.id)
    if (userIndex > -1) {
      users.value[userIndex].roles = updatedRoles
    }
  } catch (error) {
    showToast('保存失败')
  }
}

// 返回
const onClickLeft = () => {
  router.back()
}

// 获取角色标签类型
const getRoleTagType = (roleCode) => {
  const typeMap = {
    'ROLE_ADMIN': 'danger',
    'ROLE_USER': 'primary',
    'ROLE_REFEREE': 'success',
    'ROLE_TOURNAMENT_MANAGER': 'warning'
  }
  return typeMap[roleCode] || 'primary'
}

onMounted(() => {
  loadRoles()
})
</script>

<style scoped>
.user-role-assign {
  height: 100vh;
  background-color: #f7f8fa;
}

.search-box {
  padding: 8px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nickname {
  font-size: 16px;
  font-weight: bold;
}

.phone {
  font-size: 14px;
  color: #666;
}

.current-roles {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 4px;
}

.role-tag {
  margin: 0;
  white-space: nowrap;
}

:deep(.van-tag) {
  padding: 0 8px;
  font-size: 12px;
  line-height: 20px;
  border-radius: 4px;
}

.role-select-dialog {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #eee;
}

.dialog-header .title {
  font-size: 16px;
  font-weight: bold;
}

.dialog-content {
  flex: 1;
  overflow-y: auto;
}

.role-list {
  padding: 16px;
}

.role-item {
  margin-bottom: 16px;
}

.role-info {
  display: flex;
  flex-direction: column;
  margin-left: 8px;
}

.role-name {
  font-size: 14px;
  font-weight: 500;
}

.role-desc {
  font-size: 12px;
  color: #666;
  margin-top: 4px;
}

:deep(.van-checkbox__label) {
  flex: 1;
}

:deep(.van-checkbox) {
  width: 100%;
  display: flex;
  align-items: flex-start;
  padding: 12px;
  background-color: #f7f8fa;
  border-radius: 8px;
}

:deep(.van-checkbox__icon) {
  margin-top: 2px;
}

.dialog-footer {
  padding: 16px;
  border-top: 1px solid #eee;
}
</style> 