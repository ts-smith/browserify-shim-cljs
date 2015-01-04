# browserify-shim

Leiningen plugin which trivially simplifies ClojureScript development 
by searching a project for calls to (js/require "module")
and calls browserify with -r for each of them.

## Usage

Adding

    :browserify-builds {"client" "resources/public/js/client-deps.js"}

    :cljsbuild  {
        :builds  [{
            :id "client"
            :source-paths  ["src/browser" "src/shared"]}]}

will make 

$ lein browserify-shim 

search the source-paths for the corresponding id
and output a bundle containing all the dependencies mentioned in the project to
the path specified.
