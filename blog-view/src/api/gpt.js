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
export function retry(answerId,question,excludeAnswers) {
	return axios({
		url: '/fantasy/gpt/retry',
		method: 'POST',
		params: {
			answerId,
			question
		},
		data: excludeAnswers
	})
}
export function pick(answerId) {
	return axios({
		url: '/fantasy/gpt/pick',
		method: 'GET',
		params: {
			answerId
		}
	})
}