<template>
	<div>

		<el-table :data="questionList">
			<el-table-column label="序号" type="index" width="50"></el-table-column>
			<el-table-column label="问题" prop="content"></el-table-column>
      <el-table-column label="关键词" width="100">
        <template v-slot="scope">
          <el-tag v-for="k in scope.row.keywords" >{{k.content}}</el-tag>
        </template>
      </el-table-column>
			<el-table-column label="出现次数" prop="questCount" width="50"></el-table-column>
      <el-table-column label="提问时间" width="170">
        <template v-slot="scope">{{ scope.row.createTime | dateFormat }}</template>
      </el-table-column>
      <el-table-column label="回答" prop="answer.content" width="500"></el-table-column>
      <el-table-column label="是否通义生成" prop="answer.isTongyi" width="50">
          <template v-slot="scope">
            <el-tag v-if="scope.row.answer.isTongyi == true">是</el-tag>
            <el-tag v-if="scope.row.answer.isTongyi == false">否</el-tag>
          </template>
      </el-table-column>
      <el-table-column label="回答认可度" prop="answer.acceptanceCount" width="50"></el-table-column>
      <el-table-column label="回答推荐分" prop="answer.recommendScore" width="80"></el-table-column>
      <el-table-column label="回答最终分" prop="answer.finalScore" width="80"></el-table-column>
      <el-table-column label="回答状态" prop="answer.status" width="80">
        <template v-slot="scope">
          <el-tag v-if="scope.row.answer.status == true" type="success">正常</el-tag>
          <el-tag v-if="scope.row.answer.status == false" type="danger">否认</el-tag>
        </template>
      </el-table-column>
<!--			<el-table-column label="操作">-->
<!--				<template v-slot="scope">-->
<!--					<el-button type="primary" icon="el-icon-edit" size="mini" @click="showEditDialog(scope.row)">编辑回答</el-button>-->
<!--&lt;!&ndash;					<el-popconfirm title="确定删除吗？" icon="el-icon-delete" iconColor="red" @onConfirm="deleteKeywordById(scope.row.id)">&ndash;&gt;-->
<!--&lt;!&ndash;						<el-button size="mini" type="danger" icon="el-icon-delete" slot="reference">删除</el-button>&ndash;&gt;-->
<!--&lt;!&ndash;					</el-popconfirm>&ndash;&gt;-->
<!--				</template>-->
<!--			</el-table-column>-->
		</el-table>

		<!--分页-->
		<el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="queryInfo.pageNum"
		               :page-sizes="[10, 20, 30, 50]" :page-size="queryInfo.pageSize" :total="total"
		               layout="total, sizes, prev, pager, next, jumper" background>
		</el-pagination>


		<!--编辑关键词对话框-->
		<el-dialog title="编辑回答" width="50%" :visible.sync="editDialogVisible" :close-on-click-modal="false" @close="editDialogClosed">
			<!--内容主体-->
			<el-form :model="editForm" :rules="formRules" ref="editFormRef" label-width="80px">
				<el-form-item label="回答内容" prop="content">
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
	import {getDataByQuery} from '@/api/question'

	export default {
		name: "QuestionList",
		components: {
			Breadcrumb
		},
		data() {
			return {
				queryInfo: {
					pageNum: 1,
					pageSize: 10
				},
				questionList: [],
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
						this.questionList = res.data.list
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