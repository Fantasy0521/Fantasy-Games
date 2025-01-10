<template>
	<div>
		<!--content-->
		<GameItem :gameList="gameList" v-viewer/>
		<!--分页-->
		<Pagination :getGameList="getGameList" :totalPage="totalPage"/>
	</div>
</template>

<script>
	import GameItem from "./GameItem.vue";
	import Pagination from "./Pagination";

	export default {
		name: "GameList",
		components: {Pagination, GameItem},
		props: {
			getGameList: {
				type: Function,
				required: true
			},
			gameList: {
				type: Array,
				required: true
			},
			totalPage: {
				type: Number,
				required: true
			}
		}
	}
</script>

<style scoped>

</style>