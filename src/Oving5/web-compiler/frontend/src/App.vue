<script>
import axios from 'axios'
export default {
  data() {
    return {
      input: `print("hello world!")`,
      output: "",
      running: false,

    };
  },

  methods: {
    runCode() {
      this.running = true
      this.output = ""

      axios.post('http://localhost:5555/code',
          "code="+encodeURIComponent(this.input)
      )
          .then(response => {
            this.output = response.data;
            this.running = false
          })
          .catch(err => {
            this.output = err
            this.running = false
          })
    },
  }
}



</script>

<template>

  <h1>Code compiler 🐍</h1>

  <h3>Enter some Python code:</h3>
  <textarea 
      onkeydown="if(event.keyCode===9){var v=this.value,s=this.selectionStart,e=this.selectionEnd;this.value=v.substring(0, s)+'\t'+v.substring(e);this.selectionStart=this.selectionEnd=s+1;return false;}"
      spellcheck="false"
      v-model="input" >
  </textarea>

  <button @click="this.runCode" :disabled="running">Run</button>
  <p v-if="running">Compiling code...</p>
  <p v-else><br></p>
  <h3>Output:</h3>
  <textarea
      readonly
      v-model="output"
  ></textarea>

</template>

<style scoped>

  textarea {
    min-width: 100%;
    min-height: 300px;
    resize: none;
    padding: 10px 15px;

  }

  button {
    margin-top: 10px;
  }


</style>
