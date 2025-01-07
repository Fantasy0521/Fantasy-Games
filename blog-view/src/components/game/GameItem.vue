<template><!-- TODO 这里是设计具体组件div的地方-->
	<div>
		<div class="ui padded attached segment m-padded-tb-large m-margin-bottom-big m-box" v-for="item in gameList" :key="item.id">
			<div class="ui large red right corner label" v-if="item.top">
				<i class="arrow alternate circle up icon"></i>
			</div>
      <!-- TODO 移动端适配 stackable可堆叠的 mobile reversed 手机端反转-->
			<div class="ui middle aligned mobile reversed stackable">
				<div class="ui grid m-margin-lr">
					<!--标题-->
					<div class="row m-padded-tb-small">
						<h2 class="ui header m-center m-scaleup">
							<a href="javascript:;" @click.prevent="toBlog(item)" class="m-black">{{ item.name }}</a>
						</h2>
					</div>
					<!--文章简要信息-->
					<div class="row m-padded-tb-small">
						<div class="ui horizontal link list m-center">
							<div class="item m-datetime">
								<i class="small calendar icon"></i><span>发行日期: {{ item.publishDate | dateFormat('YYYY-MM-DD')}}</span>
							</div>
							<div class="item m-views">
								<i class="small eye icon"></i><span>{{ item.views }}</span>
							</div>
							<div class="item m-common-black">
								<i class="small pencil alternate icon"></i><span>开发商: {{ item.developers }}</span>
							</div>
							<div class="item m-common-black">
								<i class="small clock icon"></i><span>发行商: {{ item.publisher }}</span>
							</div>
						</div>
					</div>
					<!--分类-->
					<router-link :to="`/category/${categoryName}`" class="ui orange large ribbon label">
						<i class="small folder open icon"></i><span class="m-text-500">{{ categoryName }}</span>
					</router-link>
					<!--文章Markdown描述-->
					<div class="typo m-padded-tb-small line-numbers match-braces rainbow-braces" v-html="item.description"></div>
          <!-- 游戏封面-->
          <div class="container">
            <img class="ui small image" style="width: 300px;" :src="item.firstPicture">
          </div>
          <!--阅读全文按钮-->
					<div class="row m-padded-tb-small m-margin-top">
						<a href="javascript:;" @click.prevent="toBlog(item)" class="color-btn">查看游戏详情</a>
					</div>
					<!--横线-->
					<div class="ui section divider m-margin-lr-no"></div>
					<!--标签-->
					<div class="row m-padded-tb-no">
						<div class="column m-padding-left-no">
							<router-link :to="`/tag/${tag.name}`" class="ui tag label m-text-500 m-margin-small" :class="tag.color" v-for="(tag,index) in item.tags" :key="index">{{ tag.name }}</router-link>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
	import router from "@/router";

  export default {
		name: "GameItem",
		props: {
			gameList: {
				type: Array,
				required: true
			}
		},
    computed: {
      categoryName() {
        return this.$route.params.games
      }
    },
		methods: {
			toBlog(blog) {
        router.push(`/gameDetail/${blog.id}`)
			}
		}
	}
</script>

<style scoped>
.container {
  position: relative;
  width: 100%;
}
img {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}


</style>