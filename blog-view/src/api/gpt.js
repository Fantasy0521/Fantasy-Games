import axios from '@/plugins/axios'

export function questAI(question) {
	return axios({
		url: '/fantasy/gpt/questAI',
		method: 'GET',
		params: {
			question
		}
	})
}
export function retry(answerId,question) {
	return axios({
		url: '/fantasy/gpt/retry',
		method: 'GET',
		params: {
			answerId,
			question
		}
	})
}