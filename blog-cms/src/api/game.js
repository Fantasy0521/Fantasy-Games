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
		url: 'game',
		method: 'DELETE',
		params: {
			id
		}
	})
}

export function getCategoryAndTag() {
	return axios({
		url: 'categoryAndTag',
		method: 'GET'
	})
}

export function saveGame(game) {
	return axios({
		url: 'game',
		method: 'POST',
		data: {
			...game
		}
	})
}

export function updateTop(id, top) {
	return axios({
		url: 'game/top',
		method: 'PUT',
		params: {
			id,
			top
		}
	})
}

export function updateRecommend(id, recommend) {
	return axios({
		url: 'game/recommend',
		method: 'PUT',
		params: {
			id,
			recommend
		}
	})
}

export function updateVisibility(id, form) {
	return axios({
		url: `game/${id}/visibility`,
		method: 'PUT',
		data: {
			...form
		}
	})
}

export function getGameById(id) {
	return axios({
		url: 'game',
		method: 'GET',
		params: {
			id
		}
	})
}

export function updateGame(game) {
	return axios({
		url: 'game',
		method: 'PUT',
		data: {
			...game
		}
	})
}