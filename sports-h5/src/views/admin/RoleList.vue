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
              <van-button size="small" type="danger" @click="confirmDelete(role)">删除</van-button>
            </div>
          </template>
        </van-cell>
      </van-cell-group>
    </div>

    <!-- 添加按钮 -->
    <van-button 
      type="primary" 
      class="add-button"
      size="large"
      @click="showAddDialog = true"
    >
      添加角色
    </van-button>

    <!-- 添加/编辑角色弹窗 -->
    <van-dialog
      v-model:show="showAddDialog"
      :title="editingRole ? '编辑角色' : '添加角色'"
      show-cancel-button
      @confirm="saveRole"
    >
      <van-form>
        <van-field
          v-model="roleForm.name"
          label="角色名称"
          placeholder="请输入角色名称"
          :rules="[{ required: true, message: '请填写角色名称' }]"
        />
        <van-field
          v-model="roleForm.description"
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
        <van-checkbox-group v-model="selectedPermissions">
          <van-cell-group>
            <van-cell
              v-for="permission in permissions"
              :key="permission.id"
              :title="permission.name"
              clickable
              @click="togglePermission(permission.id)"
            >
              <template #right-icon>
                <van-checkbox :name="permission.id" ref="checkboxRefs" />
              </template>
            </van-cell>
          </van-cell-group>
        </van-checkbox-group>
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
import { showToast, showDialog } from 'vant'
import { getRoles, createRole, updateRole, deleteRole, getRolePermissions, updateRolePermissions } from '@/api/role'
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
  description: ''
})

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
    selectedPermissions.value = res.data.map(p => p.id)
  } catch (error) {
    showToast('获取角色权限失败')
  }
}

// 保存角色
const saveRole = async () => {
  try {
    if (editingRole.value) {
      await updateRole(editingRole.value.id, roleForm.value)
    } else {
      await createRole(roleForm.value)
    }
    showToast('保存成功')
    loadRoles()
    resetForm()
  } catch (error) {
    showToast('保存失败')
  }
}

// 编辑角色
const editRole = (role) => {
  editingRole.value = role
  roleForm.value = {
    name: role.name,
    description: role.description
  }
  showAddDialog.value = true
}

// 删除角色
const confirmDelete = (role) => {
  showDialog({
    title: '确认删除',
    message: `确定要删除角色 "${role.name}" 吗？`,
    showCancelButton: true,
  }).then(async () => {
    try {
      await deleteRole(role.id)
      showToast('删除成功')
      loadRoles()
    } catch (error) {
      showToast('删除失败')
    }
  })
}

// 管理权限
const managePermissions = async (role) => {
  currentRole.value = role
  showPermissionDialog.value = true
  await loadRolePermissions(role.id)
}

// 保存权限
const savePermissions = async () => {
  try {
    await updateRolePermissions(currentRole.value.id, selectedPermissions.value)
    showToast('保存成功')
    showPermissionDialog.value = false
  } catch (error) {
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
}

// 重置表单
const resetForm = () => {
  editingRole.value = null
  roleForm.value = {
    name: '',
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

.add-button {
  position: fixed;
  bottom: 20px;
  left: 16px;
  right: 16px;
}

.permission-dialog {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.permission-actions {
  padding: 16px;
  margin-top: auto;
}
</style> 