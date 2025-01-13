<template>
  <div id="gpt">
    <Chatbot/>
    <div>
      <a class="item m-common-black" @click.prevent="changeFocusMode">
        <div data-inverted="" data-tooltip="专注模式" data-position="top center">
          <i class="book icon"></i>
        </div>
      </a>
      <a class="item m-common-black" @click.prevent="toGptHtml">
        <div data-inverted="" data-tooltip="全神贯注模式" data-position="top center">
          <i class="eye icon"></i>
        </div>
      </a>
    </div>
  </div>
</template>

<script>
import Chatbot from '../../components/Chatbot.vue';
import {SET_FOCUS_MODE} from "@/store/mutations-types";
import router from "@/router";

export default {
  name: "Gpt",
  data() {
    return {
    }
  },
  components: {
    Chatbot
  },
  created() {
    // this.changeFocusMode()
  },
  methods : {
    changeFocusMode() {
      this.$store.commit(SET_FOCUS_MODE, !this.focusMode)
    },
    toGptHtml() {
      router.push(`/gptHtml`)
    }
  }
};
</script>

<style scoped>
#gpt {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
