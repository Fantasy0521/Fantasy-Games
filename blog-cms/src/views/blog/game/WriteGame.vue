<template>
  <div>
    <el-form :model="form" :rules="formRules" ref="formRef" label-position="top">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="游戏名" prop="name">
            <el-input v-model="form.name" placeholder="游戏名"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="游戏封面URL" prop="firstPicture">
            <el-input v-model="form.firstPicture" placeholder="游戏封面"></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="游戏描述" prop="description">
        <mavon-editor v-model="form.description"/>
      </el-form-item>

      <!--			<el-form-item label="帖子正文" prop="content">-->
      <!--				<mavon-editor v-model="form.content" @imgAdd="imgAdd"/>-->
      <!--			</el-form-item>-->

      <el-row :gutter="20">
        <el-col :span="20">
          <el-form-item label="游戏图片" prop="gameImages">
            <el-upload
                action="http://106.14.45.117:8055/common/upload/upload"
                list-type="picture-card"
                drag
                :on-preview="handlePictureCardPreview"
                :on-remove="handleRemove"
                :on-success="handleSuccess"
                :file-list="form.gameImages"
            >
              <i class="el-icon-plus"></i>
            </el-upload>
            <el-dialog :visible.sync="uploadDialogVisible">
              <img  width="100%"   :src="dialogImageUrl" alt="">
            </el-dialog>

          </el-form-item>
        </el-col>

      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="分类" prop="cate">
            <el-select v-model="form.cate" placeholder="请选择分类（输入可添加新分类）" :allow-create="true"
                       :filterable="true" style="width: 100%;">
              <el-option :label="item.name" :value="item.id" v-for="item in categoryList" :key="item.id"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="标签" prop="tagList">
            <el-select v-model="form.tagList" placeholder="请选择标签（输入可添加新标签）" :allow-create="true"
                       :filterable="true" :multiple="true" style="width: 100%;">
              <el-option :label="item.name" :value="item.id" v-for="item in tagList" :key="item.id"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="6">
          <el-form-item label="开发商" prop="developers">
            <el-input v-model="form.developers" placeholder="" type="text"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="发行商" prop="publisher">
            <el-input v-model="form.publisher" placeholder="" type="text"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="发行日期" prop="publishDate">
            <el-input v-model="form.publishDateString" placeholder="" type="date"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="浏览次数" prop="views">
            <el-input v-model="form.views" placeholder="请输入浏览次数（可选）默认为 0" type="number"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="收藏次数" prop="stars">
            <el-input v-model="form.stars" placeholder="请输入收藏次数（可选）默认为 0" type="number"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="官方链接" prop="officialUrl">
            <el-input v-model="form.officialUrl" placeholder="" type="text"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="下载链接" prop="downloadUrl">
            <el-input v-model="form.downloadUrl" placeholder="" type="text"></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item style="text-align: right;">
        <el-button type="primary" @click="dialogVisible=true">保存</el-button>
      </el-form-item>
    </el-form>

    <!--编辑可见性状态对话框-->
    <el-dialog title="博客可见性" width="30%" :visible.sync="dialogVisible">
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
          <el-input v-model="form.password"></el-input>
        </el-form-item>
        <el-form-item v-if="radio!==2">
          <el-row>
            <el-col :span="6">
              <el-switch v-model="form.isAppreciation" active-text="赞赏"></el-switch>
            </el-col>
            <el-col :span="6">
              <el-switch v-model="form.isRecommend" active-text="推荐"></el-switch>
            </el-col>
            <el-col :span="6">
              <el-switch v-model="form.isCommentEnabled" active-text="评论"></el-switch>
            </el-col>
            <el-col :span="6">
              <el-switch v-model="form.isTop" active-text="置顶"></el-switch>
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>
      <!--底部-->
      <span slot="footer">
				<el-button @click="dialogVisible=false">取 消</el-button>
				<el-button type="primary" @click="submit">保存</el-button>
			</span>
    </el-dialog>
  </div>
</template>

<script>
import Breadcrumb from "@/components/Breadcrumb";
import {getCategoryAndTag, saveGame, getGameById, updateGame} from '@/api/game'
import axios from "axios";
import log from "echarts/src/scale/Log";

export function getimgurl(formdata) {
  return axios({
    url: 'http:///106.14.45.117:8090/admin/upload',
    method: 'POST',
    data: formdata,
    headers: {'Content-Type': 'multipart/form-data'},
    responseType: 'text'
  });
}

export default {
  name: "WriteGame",
  components: {Breadcrumb},
  data() {
    return {
      categoryList: [],
      tagList: [],
      dialogVisible: false,
      uploadDialogVisible: false,
      radio: 2,
      form: {
        name: '',
        developers: '',
        publisher: '',
        publishDate: '',
        publishDateString: '',
        firstPicture: '',
        description: '',
        series: '',
        officialUrl: '',
        downloadUrl: '',
        cate: null,
        categoryId : null,
        tagList: [],
        gameImages: [],
        views: 0,
        stars: 0,
        appreciation: true,
        recommend: false,
        commentEnabled: true,
        top: false,
        published: true,
        isPublished: true,
        password: '',
      },
      formRules: {
        title: [{required: true, message: '请输入标题', trigger: 'change'}],
        firstPicture: [{required: true, message: '请输入首图链接', trigger: 'change'}],
        cate: [{required: true, message: '请选择分类', trigger: 'change'}],
        tagList: [{required: true, message: '请选择标签', trigger: 'change'}],
      },
      dialogImageUrl: ''
    }
  },
  watch: {
    'form.words'(newValue) {
      this.form.readTime = newValue ? Math.round(newValue / 200) : null
    },
  },
  created() {
    this.getData()
    if (this.$route.params.id) {
      this.getGame(this.$route.params.id)
    }
  },
  methods: {
    handleRemove(file, fileList) {
      this.form.gameImages.splice(this.form.gameImages.findIndex(item => item.name === file.name), 1)
    },
    handleSuccess(response, file, fileList) {
      let image = {
        name: response.data,
        rn: this.form.gameImages.length + 1,
        url: 'http://106.14.45.117:8055/common/upload/download?name='+response.data
      }
      this.form.gameImages.push(image)
      console.log(file, fileList);
    },
    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.url;
      this.uploadDialogVisible = true;
    },
    getData() {
      getCategoryAndTag().then(res => {
        if (res.code === 200) {
          this.categoryList = res.data.categories
          this.tagList = res.data.tags
        }
      })
    },
    getGame(id) {
      getGameById(id).then(res => {
        if (res.code === 200) {
          this.computeCategoryAndTag(res.data)
          this.form = res.data
          this.radio = this.form.isPublished ? ((this.form.password !== '' && this.form.password) ? 3 : 1) : 2
          console.log(this.radio)
        }
      })
    },
    computeCategoryAndTag(blog) {
      blog.cate = blog.category.id
      blog.tagList = []
      blog.tags.forEach(item => {
        blog.tagList.push(item.id)
      })
    },
    submit() {
      if (this.radio === 3 && (this.form.password === '' || this.form.password === null)) {
        return this.msgError("密码保护模式必须填写密码！")
      }
      this.$refs.formRef.validate(valid => {
        if (valid) {
          this.form.categoryId = this.form.cate
          if (this.radio === 2) {
            this.form.appreciation = false
            this.form.recommend = false
            this.form.commentEnabled = false
            this.form.top = false
            this.form.published = false
          } else {
            this.form.published = true
          }
          if (this.radio !== 3) {
            this.form.password = ''
          }
          if (this.$route.params.id) {
            // this.form.category = null
            // this.form.tags = null
            updateGame(this.form).then(res => {
              if (res.code === 200) {
                this.msgSuccess(res.msg)
                this.$router.push('/blog/game/list')
              }
            })
          } else {
            saveGame(this.form).then(res => {
              if (res.code === 200) {
                this.msgSuccess(res.msg)
                this.$router.push('/blog/game/list')
              }
            })
          }
        } else {
          this.dialogVisible = false
          return this.msgError('请填写必要的表单项')
        }
      })
    },
    // 上傳圖片script部分
    imgAdd(pos, file) {
      //这里的pos指的是在数组中的下标
      //这里创建FormData对象并将从本地获取到的file值存入。
      var formdata = new FormData();
      formdata.append("file", file);

      getimgurl(formdata)
          .then((response) => {
            // 请求成功，获取后端返回的字符串数据
            //   console.log("返回的字符串数据:", response);
          })
          .catch((error) => {
            // 请求失败，处理错误
            console.error("上传失败:", error);
          });

    }
  }
}
</script>

<style scoped>

</style>