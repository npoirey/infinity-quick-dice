module.exports = {
  lintOnSave: false,
  css: {
    loaderOptions: {
      sass: {
        data: `
          @import "~@/assets/styles.scss";
        `,
      },
    },
  },
};
