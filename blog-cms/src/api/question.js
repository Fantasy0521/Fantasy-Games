import axios from '@/util/request'

export function getDataByQuery(queryInfo) {
	return axios({
		url: 'fantasy/question/questions',
		method: 'GET',
		params: {
			...queryInfo
		}
	})
}

export function deleteQuestionById(id) {
	return axios({
		url: 'fantasy/question',
		method: 'DELETE',
		params: {
			id
		}
	})
}


export function saveQuestion(question) {
	return axios({
		url: 'fantasy/question',
		method: 'POST',
		data: {
			...question
		}
	})
}

export function updateTop(id, top) {
	return axios({
		url: 'fantasy/question/top',
		method: 'PUT',
		params: {
			id,
			top
		}
	})
}

export function getQuestionById(id) {
	return axios({
		url: 'fantasy/question',
		method: 'GET',
		params: {
			id
		}
	})
}

export function updateQuestion(question) {
	return axios({
		url: 'fantasy/question',
		method: 'PUT',
		data: {
			...question
		}
	})
}