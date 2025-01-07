import axios from '@/util/request'

export function getDataByQuery(queryInfo) {
	return axios({
		url: 'fantasy/game/games',
		method: 'GET',
		params: {
			...queryInfo
		}
	})
}

export function deleteGameById(id) {
	return axios({
		url: 'fantasy/game',
		method: 'DELETE',
		params: {
			id
		}
	})
}

export function getCategoryAndTag() {
	return axios({
		url: 'fantasy/game/categoryAndTag',
		method: 'GET'
	})
}

export function saveGame(game) {
	return axios({
		url: 'fantasy/game',
		method: 'POST',
		data: {
			...game
		}
	})
}

export function updateTop(id, top) {
	return axios({
		url: 'fantasy/game/top',
		method: 'PUT',
		params: {
			id,
			top
		}
	})
}

export function updateRecommend(id, recommend) {
	return axios({
		url: 'fantasy/game/recommend',
		method: 'PUT',
		params: {
			id,
			recommend
		}
	})
}

export function updateVisibility(id, form) {
	return axios({
		url: `fantasy/game/${id}/visibility`,
		method: 'PUT',
		data: {
			...form
		}
	})
}

export function getGameById(id) {
	return axios({
		url: 'fantasy/game',
		method: 'GET',
		params: {
			id
		}
	})
}

export function updateGame(game) {
	return axios({
		url: 'fantasy/game',
		method: 'PUT',
		data: {
			...game
		}
	})
}