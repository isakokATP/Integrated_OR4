export const vTrim = {
  mounted: (el) => {
    el.addEventListener("blur", () => {
      if (el.value) {
        el.value = el.value.trim();
        // Trigger input event to update v-model
        el.dispatchEvent(new Event("input"));
      }
    });
  },
};
