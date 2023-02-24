<script setup>

</script>

<template>

  <h1>Code compiler</h1>

  <textarea 
  onkeydown="if(event.keyCode===9){var v=this.value,s=this.selectionStart,e=this.selectionEnd;this.value=v.substring(0, s)+'\t'+v.substring(e);this.selectionStart=this.selectionEnd=s+1;return false;}"
  spellcheck="false"
  >
  </textarea>
  <button>Run</button>
  <h3>Output:</h3>
  <div class="output">
    <p>Hello world</p>

  </div>

</template>

<style scoped>

  textarea {
    min-width: 100%;
    min-height: 300px;
    resize: none;

  }

  .output {
    border-width: 1px;
    border-style: solid;
    min-width: 100%;
    min-height: 150px;
    text-align: left;
    padding-left: 4%;

  }





</style>
