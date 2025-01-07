import axios from '@/util/request'

export function getMomentListByQuery(queryInfo) {
	return axios({
		url: 'fantasy/moment/moments',
		method: 'GET',
		params: {
			...queryInfo
		}
	})
}

export function updatePublished(id, published) {
	return axios({
		url: 'fantasy/moment/moment/published',
		method: 'PUT',
		params: {
			id,
			published
		}
	})
}

export function getMomentById(id) {
	return axios({
		url: 'fantasy/moment',
		method: 'GET',
		params: {
			id
		}
	})
}

export function deleteMomentById(id) {
	return axios({
		url: 'fantasy/moment',
		method: 'DELETE',
		params: {
			id
		}
	})
}

export function saveMoment(moment) {
	return axios({
		url: 'fantasy/moment',
		method: 'POST',
		data: {
			...moment
		}
	})
}

export function updateMoment(moment) {
	return axios({
		url: 'fantasy/moment',
		method: 'PUT',
		data: {
			...moment
		}
	})
}