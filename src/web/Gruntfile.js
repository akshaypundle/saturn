module.exports = function(grunt) {
  "use strict";

  grunt.initConfig({
	current: ".",
    lib: "<%= current %>/libraries",
    src: "<%= current %>/src",
    typings: "<%= current %>/typings",

    clean: {
      build: ["<%= src %>/**/*.js", "<%= src %>/**/*.js.map", "<%= src %>/app.css"]
    },

    csslint: {
      lint: {
        options: {
          "adjoining-classes": false,
          "box-model": false,
          "box-sizing": false,
          "bulletproof-font-face": false,
          "display-property-grouping": false,
          "duplicate-background-images": false,
          "fallback-colors": false,
          "floats": false,
          "font-faces": false,
          "font-sizes": false,
          "gradients": false,
          "ids": false,
          "import": false,
          "important": false,
          "known-properties": false,
          "outline-none": false,
          "overqualified-elements": false,
          "qualified-headings": false,
          "unique-headings": false,
          "vendor-prefix": false,
          "zero-units": false
        },
        src: [
          "<%= src %>/app.css"
        ]
      }
    },

    'http-server': {
      'dev': {
        root: "<%= current %>",
        port: 8080,
        host: "127.0.0.1",
        showDir : true,
        autoIndex: true,
        ext: "html",
        runInBackground: true,
      }
    },

    less: {
      compile: {
        files: {
          "<%= src %>/app.css": "<%= src %>/app.less"
        }
      }
    },

    ngtemplates: {
      "saturn.app": {
        dest: "<%= src %>/templates.js",
        src: "<%= src %>/**/*.html",
      }
    },

    ts: {
      compile: {
        options: {
          comments: true,
          module: "amd",
          noImplicitAny: true,
          sourceMap: true,
          target: "es5"
        },
        src: ["<%= src %>/**/*.ts", "<%= typings %>/**/*.d.ts", "<%= lib %>/**/*.d.ts"]
      }
    },

    tslint: {
      options: {
        configuration: grunt.file.readJSON("tslint.json")
      },
      files: ["<%= src %>/**/*.ts"]
    },

    watch: {
      options: {
        force: true
      },
      gruntfile: {
        files: "Gruntfile.js",
        tasks: ["build"]
      },
      ts: {
        files: ["<%= src %>/**/*.ts", "<%= typings %>/**/*.d.ts", "<%= lib %>/**/*.d.ts"],
        tasks: ["tslint", "ts"]
      },
      less: {
        files: ["<%= src %>/**/*.less"],
        tasks: ["less", "csslint"]
      },
      templates: {
        files: ["<%= src %>/**/*.html"],
        tasks: ["ngtemplates"]
      }
    }
  });

  grunt.loadNpmTasks("grunt-angular-templates");
  grunt.loadNpmTasks("grunt-contrib-clean");
  grunt.loadNpmTasks("grunt-contrib-csslint");
  grunt.loadNpmTasks("grunt-contrib-less");
  grunt.loadNpmTasks("grunt-contrib-watch");
  grunt.loadNpmTasks("grunt-http-server");
  grunt.loadNpmTasks("grunt-ts");
  grunt.loadNpmTasks("grunt-tslint");


  grunt.registerTask("default", "build");

  grunt.registerTask("build",
    [
      "clean",
      "ngtemplates",
      "less",
      "csslint",
      "tslint",
      "ts"
    ]
  );
  grunt.registerTask("devWatch",
    [
      "build",
      "http-server:dev",
      "watch"
    ]
  );
};

