<template>
	<div>
		<!--添加-->
		<el-row :gutter="10">
			<el-col :span="6">
				<el-button type="primary" size="small" icon="el-icon-plus" @click="addDialogVisible=true">添加关键词</el-button>
			</el-col>
		</el-row>

		<el-table :data="keywordList">
			<el-table-column label="序号" type="index" width="50"></el-table-column>
			<el-table-column label="名称" prop="content"></el-table-column>
			<el-table-column label="权重" prop="weight"></el-table-column>
			<el-table-column label="出现次数" prop="keywordCount"></el-table-column>
			<el-table-column label="类型">
        <template v-slot="scope">
          <el-tag v-if="scope.row.weight > 0" type="success">关键词</el-tag>
          <el-tag v-if="scope.row.weight === 0" type="info">可能的关键词</el-tag>
          <el-tag v-if="scope.row.weight < 0" type="danger">停用词</el-tag>
        </template>
      </el-table-column>
			<el-table-column label="操作">
				<template v-slot="scope">
					<el-button type="primary" icon="el-icon-edit" size="mini" @click="showEditDialog(scope.row)">编辑</el-button>
					<el-popconfirm title="确定删除吗？" icon="el-icon-delete" iconColor="red" @onConfirm="deleteKeywordById(scope.row.id)">
						<el-button size="mini" type="danger" icon="el-icon-delete" slot="reference">删除</el-button>
					</el-popconfirm>
				</template>
			</el-table-column>
		</el-table>

		<!--分页-->
		<el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="queryInfo.pageNum"
		               :page-sizes="[10, 20, 30, 50]" :page-size="queryInfo.pageSize" :total="total"
		               layout="total, sizes, prev, pager, next, jumper" background>
		</el-pagination>

		<!--添加关键词对话框-->
		<el-dialog title="添加关键词" width="50%" :visible.sync="addDialogVisible" :close-on-click-modal="false" @close="addDialogClosed">
			<!--内容主体-->
			<el-form :model="addForm" :rules="formRules" ref="addFormRef" label-width="80px">
				<el-form-item label="关键词名称" prop="content">
					<el-input v-model="addForm.content"></el-input>
				</el-form-item>
				<el-form-item label="权重" prop="weight">
					<el-input v-model="addForm.weight"></el-input>
				</el-form-item>
				<el-form-item label="出现次数" prop="keywordCount">
					<el-input v-model="addForm.keywordCount"></el-input>
				</el-form-item>
			</el-form>
			<!--底部-->
			<span slot="footer">
				<el-button @click="addDialogVisible=false">取 消</el-button>
				<el-button type="primary" @click="addKeyword">确 定</el-button>
			</span>
		</el-dialog>

		<!--编辑关键词对话框-->
		<el-dialog title="编辑关键词" width="50%" :visible.sync="editDialogVisible" :close-on-click-modal="false" @close="editDialogClosed">
			<!--内容主体-->
			<el-form :model="editForm" :rules="formRules" ref="editFormRef" label-width="80px">
				<el-form-item label="关键词名称" prop="content">
					<el-input v-model="editForm.content"></el-input>
				</el-form-item>
        <el-form-item label="权重" prop="weight">
          <el-input v-model="editForm.weight"></el-input>
        </el-form-item>
        <el-form-item label="出现次数" prop="keywordCount">
          <el-input v-model="editForm.keywordCount"></el-input>
        </el-form-item>
			</el-form>
			<!--底部-->
			<span slot="footer">
				<el-button @click="editDialogVisible=false">取 消</el-button>
				<el-button type="primary" @click="editKeyword">确 定</el-button>
			</span>
		</el-dialog>
	</div>
</template>

<script>
	import Breadcrumb from "@/components/Breadcrumb";
	import {getDataByQuery, getKeywordById, updateKeyword, deleteKeywordById, saveKeyword, updateTop} from '@/api/keyword'

	export default {
		name: "KeywordList",
		components: {
			Breadcrumb
		},
		data() {
			return {
				queryInfo: {
					pageNum: 1,
					pageSize: 10
				},
				keywordList: [],
				total: 0,
				addDialogVisible: false,
				editDialogVisible: false,
				addForm: {
					name: '',
          weight: 0,
          keywordCount: 0
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
						this.keywordList = res.data.list
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
			addKeyword() {
				this.$refs.addFormRef.validate(valid => {
					if (valid) {
						saveKeyword(this.addForm).then(res => {
							if (res.code === 200) {
								this.msgSuccess(res.msg)
								this.addDialogVisible = false
								this.getData()
							}
						})
					}
				})
			},
			editKeyword() {
				this.$refs.editFormRef.validate(valid => {
					if (valid) {
            updateKeyword(this.editForm).then(res => {
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
			deleteKeywordById(id) {
				deleteKeywordById(id).then(res => {
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