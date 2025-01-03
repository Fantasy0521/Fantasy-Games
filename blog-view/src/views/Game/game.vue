<template>
	<div>
		<div class="ui top segment" style="text-align: center">
<!--			<h2 class="m-text-500">分类 {{ categoryName }} 下的游戏</h2>-->
      <div class="ui icon input" style="width: 400px">
        <input v-model="keyword" type="text" placeholder="Search...">
        <i class="circular search link icon" @click="getGameList(1)"></i>
      </div>
		</div>
		<GameList :getGameList="getGameList" :gameList="gameList" :totalPage="totalPage"/>
	</div>
</template>

<script>
	import GameList from "@/components/game/GameList";
	import {getGameListByCategoryName} from "@/api/game";

	export default {
		name: "game",
		components: {GameList},
		data() {
			return {
				gameList: [],
				totalPage: 0,
        keyword:""
			}
		},
		watch: {
			//在当前组件被重用时，要重新获取博客列表
			'$route.fullPath'() {
				if (this.$route.name === 'game') {
					this.getGameList()
				}
			}
		},
		created() {
			this.getGameList()
		},
		computed: {
			categoryName() {
				return this.$route.params.games
			}
		},
		methods: {
			getGameList(pageNum) {
        getGameListByCategoryName(this.categoryName,this.keyword, pageNum).then(res => {
					if (res.code === 200) {
						this.gameList = res.data.list
						this.totalPage = res.data.totalPage
						this.$nextTick(() => {
							Prism.highlightAll()
						})
					} else {
						this.msgError(res.msg)
					}
				}).catch(() => {
					this.msgError("请求失败")
				})
			}
		}
	}
</script>

<style scoped>

</style>