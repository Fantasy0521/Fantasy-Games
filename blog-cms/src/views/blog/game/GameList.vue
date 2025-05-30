<template>
	<div>
		<!--搜索-->
		<el-row>
			<el-col :span="8">
				<el-input placeholder="请输入标题" v-model="queryInfo.title" :clearable="true" @clear="search" @keyup.native.enter="search" size="small" style="min-width: 500px">
					<el-select v-model="queryInfo.categoryId" slot="prepend" placeholder="请选择分类" :clearable="true" @change="search" style="width: 160px">
						<el-option :label="item.name" :value="item.id" v-for="item in categoryList" :key="item.id"></el-option>
					</el-select>
					<el-button slot="append" icon="el-icon-search" @click="search"></el-button>
					<el-button slot="append" icon="el-icon-plus" @click="addGame">添加游戏</el-button>
				</el-input>
			</el-col>
		</el-row>

		<el-table :data="blogList">
			<el-table-column label="序号" type="index" width="50"></el-table-column>
			<el-table-column label="游戏名" prop="name" show-overflow-tooltip width="150"></el-table-column>
			<el-table-column label="分类" prop="category.name" width="150"></el-table-column>
      <el-table-column label="游戏封面" width="80">
        <template v-slot="scope">
          <el-avatar shape="square" :size="60" fit="contain" :src="scope.row.firstPicture"></el-avatar>
        </template>
      </el-table-column>
      <el-table-column label="开发商" prop="developers" width="150"></el-table-column>
      <el-table-column label="发行商" prop="publisher" width="150"></el-table-column>
      <el-table-column label="发行日期" width="170">
        <template v-slot="scope">{{ scope.row.publishDate | dateFormat }}</template>
      </el-table-column>
      <el-table-column label="浏览量" prop="views" width="100"></el-table-column>
      <el-table-column label="收藏量" prop="stars" width="100"></el-table-column>
			<el-table-column label="置顶" width="80">
				<template v-slot="scope">
					<el-switch v-model="scope.row.isTop" @change="blogTopChanged(scope.row)"></el-switch>
				</template>
			</el-table-column>
			<el-table-column label="推荐" width="80">
				<template v-slot="scope">
					<el-switch v-model="scope.row.isRecommend" @change="blogRecommendChanged(scope.row)"></el-switch>
				</template>
			</el-table-column>
			<el-table-column label="可见性" width="100">
				<template v-slot="scope">
					<el-link icon="el-icon-edit" :underline="false" @click="editGameVisibility(scope.row)">
						{{ scope.row.isPublished ? ((scope.row.password !== '' && scope.row.password) ? '密码保护' : '公开') : '私密' }}
					</el-link>
				</template>
			</el-table-column>
			<el-table-column label="操作" width="200">
				<template v-slot="scope">
					<el-button type="primary" icon="el-icon-edit" size="mini" @click="goGameEditPage(scope.row.id)">编辑</el-button>
					<el-popconfirm title="确定删除吗？" icon="el-icon-delete" iconColor="red" @onConfirm="deleteGameById(scope.row.id)">
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

		<!--编辑可见性状态对话框-->
		<el-dialog title="可见性" width="30%" :visible.sync="dialogVisible">
			<!--内容主体-->
			<el-form label-width="50px" @submit.native.prevent>
				<el-form-item>
					<el-radio-group v-model="radio">
						<el-radio :label="1">公开</el-radio>
						<el-radio :label="2">私密</el-radio>
						<el-radio :label="3">密码保护</el-radio>
					</el-radio-group>
				</el-form-item>
				<el-form-item label="密码" v-if="radio===3">
					<el-input v-model="visForm.password"></el-input>
				</el-form-item>
				<el-form-item v-if="radio!==2">
					<el-row>
						<el-col :span="6">
							<el-switch v-model="visForm.appreciation" active-text="赞赏"></el-switch>
						</el-col>
						<el-col :span="6">
							<el-switch v-model="visForm.recommend" active-text="推荐"></el-switch>
						</el-col>
						<el-col :span="6">
							<el-switch v-model="visForm.commentEnabled" active-text="评论"></el-switch>
						</el-col>
						<el-col :span="6">
							<el-switch v-model="visForm.top" active-text="置顶"></el-switch>
						</el-col>
					</el-row>
				</el-form-item>
			</el-form>
			<!--底部-->
			<span slot="footer">
				<el-button @click="dialogVisible=false">取 消</el-button>
				<el-button type="primary" @click="saveVisibility">保存</el-button>
			</span>
		</el-dialog>
	</div>
</template>

<script>
	import Breadcrumb from "@/components/Breadcrumb";
	import {getDataByQuery, deleteGameById, updateTop, updateRecommend, updateVisibility} from '@/api/game'
	import {getData} from '@/api/category'

	export default {
		name: "GameList",
		components: {Breadcrumb},
		data() {
			return {
				queryInfo: {
					title: '',
					categoryId: null,
					pageNum: 1,
					pageSize: 10
				},
				blogList: [],
				categoryList: [],
				total: 0,
				dialogVisible: false,
				blogId: 0,
				radio: 1,
				visForm: {
					appreciation: false,
					recommend: false,
					commentEnabled: false,
					top: false,
					published: false,
					password: '',
				}
			}
		},
		created() {
			this.getData()
      this.getCategoryData()
		},
		methods: {
			getData() {
				getDataByQuery(this.queryInfo).then(res => {
					if (res.code === 200) {
						this.blogList = res.data.list
						// this.categoryList = res.data.categories
						this.total = res.data.total
					}
				})
			},
			getCategoryData() {
        let queryInfo = {
          pageNum: 1,
          pageSize: 100
        }
        getData(queryInfo).then(res => {
					if (res.code === 200) {
						// this.blogList = res.data.list
						this.categoryList = res.data.list
						// this.total = res.data.total
					}
				})
			},
			search() {
				this.queryInfo.pageNum = 1
				this.queryInfo.pageSize = 10
				this.getData()
			},
      addGame() {
        this.$router.push(`/blog/game/write/`)
      },
			//切换游戏置顶状态
			blogTopChanged(row) {
				updateTop(row.id, row.isTop).then(res => {
					if (res.code === 200) {
						this.msgSuccess(res.msg);
					}
				})
			},
			//切换游戏推荐状态
			blogRecommendChanged(row) {
				updateRecommend(row.id, row.isRecommend).then(res => {
					if (res.code === 200) {
						this.msgSuccess(res.msg);
					}
				})
			},
			//编辑可见性
			editGameVisibility(row) {
				this.visForm = {
					appreciation: row.isAppreciation,
					recommend: row.isRecommend,
					commentEnabled: row.isCommentEnabled,
					top: row.isTop,
					published: row.isPublished,
					password: row.password,
				}
				this.blogId = row.id
        this.radio = this.visForm.published ? ((this.visForm.password !== '' && this.visForm.password) ? 3 : 1) : 2
				this.dialogVisible = true
			},
			//修改可见性
			saveVisibility() {
				if (this.radio === 3 && (this.visForm.password === '' || this.visForm.password === null)) {
					return this.msgError("密码保护模式必须填写密码！")
				}
				if (this.radio === 2) {
					this.visForm.appreciation = false
					this.visForm.recommend = false
					this.visForm.commentEnabled = false
					this.visForm.top = false
					this.visForm.published = false
				} else {
					this.visForm.published = true
				}
				if (this.radio !== 3) {
					this.visForm.password = ''
				}
				updateVisibility(this.blogId, this.visForm).then(res => {
					if (res.code === 200) {
						this.msgSuccess(res.msg)
						this.getData()
						this.dialogVisible = false
					}
				})
			},
			//监听 pageSize 改变事件
			handleSizeChange(newSize) {
				this.queryInfo.pageSize = newSize
				this.getData()
			},
			//监听页码改变的事件
			handleCurrentChange(newPage) {
				this.queryInfo.pageNum = newPage
				this.getData()
			},
			goGameEditPage(id) {
				this.$router.push(`/blog/game/edit/${id}`)
			},
			deleteGameById(id) {
				this.$confirm('此操作将永久删除该游戏<strong style="color: red">及其所有评论</strong>，是否删除?<br>建议将游戏置为<strong style="color: red">私密</strong>状态！', '提示', {
					confirmButtonText: '确定',
					cancelButtonText: '取消',
					type: 'warning',
					dangerouslyUseHTMLString: true
				}).then(() => {
					deleteGameById(id).then(res => {
						if (res.code === 200) {
							this.msgSuccess(res.msg)
							this.getData()
						}
					})
				}).catch(() => {
					this.$message({
						type: 'info',
						message: '已取消删除'
					})
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