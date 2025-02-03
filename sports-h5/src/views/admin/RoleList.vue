<template>
  <div class="role-list">
    <van-nav-bar
      title="角色管理"
      left-text="返回"
      left-arrow
      @click-left="onClickLeft"
    />
    
    <!-- 角色列表 -->
    <div class="role-items">
      <van-cell-group>
        <van-cell v-for="role in roles" :key="role.id">
          <template #title>
            <span class="role-name">{{ role.name }}</span>
          </template>
          <template #label>
            <span class="role-desc">{{ role.description }}</span>
          </template>
          <template #right-icon>
            <div class="action-buttons">
              <van-button size="small" type="primary" @click="editRole(role)">编辑</van-button>
              <van-button size="small" type="info" @click="managePermissions(role)">权限</van-button>
            </div>
          </template>
        </van-cell>
      </van-cell-group>
    </div>

    <!-- 编辑角色弹窗 -->
    <van-dialog
      v-model:show="showAddDialog"
      title="编辑角色"
      show-cancel-button
      @confirm="handleSubmit"
      @cancel="resetForm"
    >
      <van-form ref="roleFormRef">
        <van-field
          v-model="roleForm.name"
          name="name"
          label="角色名称"
          placeholder="请输入角色名称"
          :rules="[{ required: true, message: '请填写角色名称' }]"
        />
        <van-field
          v-model="roleForm.code"
          name="code"
          label="角色代码"
          placeholder="请输入角色代码，如：ROLE_REFEREE"
          :rules="[{ required: true, message: '请填写角色代码' }]"
        />
        <van-field
          v-model="roleForm.description"
          name="description"
          label="角色描述"
          type="textarea"
          placeholder="请输入角色描述"
          :rules="[{ required: true, message: '请填写角色描述' }]"
        />
      </van-form>
    </van-dialog>

    <!-- 权限管理弹窗 -->
    <van-popup
      v-model:show="showPermissionDialog"
      position="right"
      :style="{ width: '80%', height: '100%' }"
    >
      <div class="permission-dialog">
        <van-nav-bar
          :title="currentRole?.name + ' - 权限管理'"
          left-text="返回"
          left-arrow
          @click-left="showPermissionDialog = false"
        />
        <div class="permission-content">
          <van-checkbox-group v-model="selectedPermissions">
            <van-cell-group>
              <van-cell
                v-for="permission in permissions"
                :key="permission.id"
                :title="permission.name"
                :label="permission.description"
                clickable
                @click="togglePermission(permission.id)"
              >
                <template #right-icon>
                  <van-checkbox 
                    :name="permission.id" 
                    ref="checkboxRefs"
                    :checked="selectedPermissions.includes(permission.id)"
                  />
                </template>
              </van-cell>
            </van-cell-group>
          </van-checkbox-group>
        </div>
        <div class="permission-actions">
          <van-button type="primary" block @click="savePermissions">保存权限</van-button>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { getRoles, updateRole, getRolePermissions, updateRolePermissions } from '@/api/role'
import { getPermissions } from '@/api/permission'

const router = useRouter()
const roles = ref([])
const showAddDialog = ref(false)
const showPermissionDialog = ref(false)
const editingRole = ref(null)
const currentRole = ref(null)
const permissions = ref([])
const selectedPermissions = ref([])

const roleForm = ref({
  name: '',
  code: '',
  description: ''
})

const roleFormRef = ref(null)

// 获取角色列表
const loadRoles = async () => {
  try {
    const res = await getRoles()
    roles.value = res.data
  } catch (error) {
    showToast('获取角色列表失败')
  }
}

// 获取权限列表
const loadPermissions = async () => {
  try {
    const res = await getPermissions()
    permissions.value = res.data
  } catch (error) {
    showToast('获取权限列表失败')
  }
}

// 加载角色的权限
const loadRolePermissions = async (roleId) => {
  try {
    const res = await getRolePermissions(roleId)
    // 确保返回的权限数据是数组
    const permissions = Array.isArray(res.data) ? res.data : []
    // 设置选中的权限ID
    selectedPermissions.value = permissions.map(p => p.id)
    console.log('已选中的权限:', selectedPermissions.value)
  } catch (error) {
    console.error('获取角色权限失败:', error)
    showToast('获取角色权限失败')
  }
}

// 处理表单提交
const handleSubmit = async () => {
  try {
    // 验证表单
    await roleFormRef.value?.validate()
    
    const formData = {
      name: roleForm.value.name,
      code: roleForm.value.code,
      description: roleForm.value.description
    }
    
    console.log('提交表单数据:', formData)
    await updateRole(editingRole.value.id, formData)
    
    showToast('保存成功')
    loadRoles()
    resetForm()
    return true
  } catch (error) {
    console.error('保存角色失败:', error)
    showToast(error.response?.data?.message || error.message || '保存失败')
    return false
  }
}

// 编辑角色
const editRole = (role) => {
  editingRole.value = role
  roleForm.value = {
    name: role.name,
    code: role.code,
    description: role.description
  }
  showAddDialog.value = true
}

// 管理权限
const managePermissions = async (role) => {
  currentRole.value = role
  // 先加载所有权限
  await loadPermissions()
  // 再加载角色的权限
  await loadRolePermissions(role.id)
  showPermissionDialog.value = true
}

// 保存权限
const savePermissions = async () => {
  try {
    // 确保所有ID都是数字类型
    const permissionIds = selectedPermissions.value.map(Number)
    await updateRolePermissions(currentRole.value.id, permissionIds)
    showToast('保存成功')
    showPermissionDialog.value = false
  } catch (error) {
    console.error('保存权限失败:', error)
    showToast('保存失败')
  }
}

// 切换权限选择
const togglePermission = (permissionId) => {
  const index = selectedPermissions.value.indexOf(permissionId)
  if (index > -1) {
    selectedPermissions.value.splice(index, 1)
  } else {
    selectedPermissions.value.push(permissionId)
  }
  console.log('当前选中的权限:', selectedPermissions.value)
}

// 重置表单
const resetForm = () => {
  console.log('重置表单')
  editingRole.value = null
  roleForm.value = {
    name: '',
    code: '',
    description: ''
  }
}

// 返回
const onClickLeft = () => {
  router.back()
}

onMounted(() => {
  loadRoles()
  loadPermissions()
})
</script>

<style scoped>
.role-list {
  height: 100vh;
  background-color: #f7f8fa;
}

.role-items {
  margin-top: 12px;
}

.role-name {
  font-size: 16px;
  font-weight: bold;
}

.role-desc {
  font-size: 14px;
  color: #666;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.permission-dialog {
  height: 100%;
  display: flex;
  flex-direction: column;
  position: relative;
}

.permission-content {
  flex: 1;
  overflow-y: auto;
  padding-bottom: 80px;
}

.permission-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 16px;
  background-color: #fff;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.05);
  z-index: 99;
}
</style> 