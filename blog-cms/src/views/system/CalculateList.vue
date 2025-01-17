<template>
	<div>

		<el-table :data="calculateList">
			<el-table-column label="序号" type="index" width="50"></el-table-column>
			<el-table-column label="认可度系数" prop="acceptanceScoreCoe" width="200"></el-table-column>
			<el-table-column label="推荐分系数" prop="recommendScoreCoe" width="200"></el-table-column>
      <el-table-column label="匹配度系数" prop="keywordScoreCoe" width="200"></el-table-column>
      <el-table-column label="模型" prop="type" width="50"></el-table-column>
      <el-table-column label="公式" >最终分 = 认可度系数 * 回答认可度 + 推荐分系数 * 游戏推荐分 + 匹配度系数 * 关键词匹配度</el-table-column>
      <el-table-column label="用户认可度" >61.39%</el-table-column>
			<el-table-column label="操作">
				<template v-slot="scope">
					<el-button type="primary" icon="el-icon-edit" size="mini" @click="showEditDialog(scope.row)">编辑</el-button>
<!--					<el-popconfirm title="确定删除吗？" icon="el-icon-delete" iconColor="red" @onConfirm="deleteCalculateById(scope.row.id)">-->
<!--						<el-button size="mini" type="danger" icon="el-icon-delete" slot="reference">删除</el-button>-->
<!--					</el-popconfirm>-->
				</template>
			</el-table-column>
		</el-table>

		<!--分页-->
		<el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="queryInfo.pageNum"
		               :page-sizes="[10, 20, 30, 50]" :page-size="queryInfo.pageSize" :total="total"
		               layout="total, sizes, prev, pager, next, jumper" background>
		</el-pagination>


		<!--编辑关键词对话框-->
		<el-dialog title="编辑算法配置" width="50%" :visible.sync="editDialogVisible" :close-on-click-modal="false" @close="editDialogClosed">
			<!--内容主体-->
			<el-form :model="editForm" :rules="formRules" ref="editFormRef" label-width="80px">
				<el-form-item label="认可度系数" prop="acceptanceScoreCoe">
					<el-input v-model="editForm.acceptanceScoreCoe"></el-input>
				</el-form-item>
        <el-form-item label="推荐分系数" prop="recommendScoreCoe">
          <el-input v-model="editForm.recommendScoreCoe"></el-input>
        </el-form-item>
        <el-form-item label="匹配度系数" prop="keywordScoreCoe">
          <el-input v-model="editForm.keywordScoreCoe"></el-input>
        </el-form-item>
        <el-alert
            title="请谨慎修改算法系数，这些系数将影响对应分值在最终分中的占比"
            type="warning">
        </el-alert>
			</el-form>
			<!--底部-->
			<span slot="footer">
				<el-button @click="editDialogVisible=false">取 消</el-button>
				<el-button type="primary" @click="editCalculate">确 定</el-button>
			</span>
		</el-dialog>
	</div>
</template>

<script>
	import Breadcrumb from "@/components/Breadcrumb";
	import {getDataByQuery,updateCalculate} from '@/api/calculate'

	export default {
		name: "CalculateList",
		components: {
			Breadcrumb
		},
		data() {
			return {
				queryInfo: {
					pageNum: 1,
					pageSize: 10
				},
				calculateList: [],
				total: 0,
				addDialogVisible: false,
				editDialogVisible: false,
				addForm: {
					name: '',
          weight: 0,
          calculateCount: 0
				},
				editForm: {},
				formRules: {
					name: [{required: true, message: '请输入关键词名称', trigger: 'blur'}]
				}
			}
		},
		created() {
			this.getData()
		},
		methods: {
			getData() {
        getDataByQuery(this.queryInfo).then(res => {
					if (res.code === 200) {
						this.calculateList = res.data.list
						this.total = res.data.total
					}
				})
			},
			//监听 pageSize 改变事件
			handleSizeChange(newSize) {
				this.queryInfo.pageSize = newSize
				this.getData()
			},
			//监听页码改变事件
			handleCurrentChange(newPage) {
				this.queryInfo.pageNum = newPage
				this.getData()
			},
			addDialogClosed() {
				this.$refs.addFormRef.resetFields()
			},
			editDialogClosed() {
				this.editForm = {}
				this.$refs.editFormRef.resetFields()
			},
			addCalculate() {
				this.$refs.addFormRef.validate(valid => {
					if (valid) {
						saveCalculate(this.addForm).then(res => {
							if (res.code === 200) {
								this.msgSuccess(res.msg)
								this.addDialogVisible = false
								this.getData()
							}
						})
					}
				})
			},
			editCalculate() {
				this.$refs.editFormRef.validate(valid => {
					if (valid) {
            updateCalculate(this.editForm).then(res => {
							if (res.code === 200) {
								this.msgSuccess(res.msg)
								this.editDialogVisible = false
								this.getData()
							}
						})
					}
				})
			},
			showEditDialog(row) {
				//row 中没有对象(blogs是表单不需要的属性)，直接拓展运算符深拷贝一份(拓展运算符不能深拷贝对象，只能拷贝引用)
				//如果直接赋值，则为引用，表格上的数据也会随对话框中数据的修改而实时改变
				this.editForm = {...row}
				this.editDialogVisible = true
			},
			deleteCalculateById(id) {
				deleteCalculateById(id).then(res => {
					if (res.code === 200) {
						this.msgSuccess(res.msg)
						this.getData()
					}
				})
			}
		}
	}
</script>

<style scoped>
	.el-button + span {
		margin-left: 10px;
	}
</style>