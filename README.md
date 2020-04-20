# Content Aware Image Resizing

Implementation of Seam carving algorithm for content aware image resizing with Java.

## Presentation

The purpose of the tool is to reduce to width of an image without introducing distortion in the aspect ratio like standards resizing methods.
The method is based on the [seam carving](https://en.wikipedia.org/wiki/Seam_carving) techniques implemented with flow graph and the [Ford-Fulkerson algorithm](https://en.wikipedia.org/wiki/Ford%E2%80%93Fulkerson_algorithm).

The tool can only reduce width of images but with an implementation of another graph generation based on vertical gradient the reduction of the height of the image is possible (check the documentation for further details).

### Example

Here an example of input :

![tower](./doc/results/tower.png)

The left image is the classical resizing method and the right image is the result of the seam carving :

![resize-tower](./doc/results/tower_resize.png) ![result-tower](./doc/results/tower_result.png)

As we can see the left image is flattened while there is no distortion in the right image.


## Usage

* Compilation :
	- `mkdir bin/`
	- `javac -d bin/ @files`

* Generate documentation :
	- `javadoc -d docs/api @files`

* Run :
	- `java -cp bin/ cair.main.Main`
