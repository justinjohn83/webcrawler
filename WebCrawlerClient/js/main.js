//
//  main.js
//
//  A project template for using arbor.js
//

(function($){

  var renderer = function(canvas){
    var canvas = $(canvas).get(0);
    var ctx = canvas.getContext("2d");
    var gfx = Graphics(canvas);
    var particleSystem;

    var that = {};
     that.init = function(system){
        //
        // the particle system will call the init function once, right before the
        // first frame is to be drawn. it's a good place to set up the canvas and
        // to pass the canvas size to the particle system
        //
        // save a reference to the particle system for use in the .redraw() loop
        particleSystem = system;

        // inform the system of the screen dimensions so it can map coords for us.
        // if the canvas is ever resized, screenSize should be called again with
        // the new dimensions
        particleSystem.screenSize(canvas.width, canvas.height) ;
        particleSystem.screenPadding(80); // leave an extra 80px of whitespace per side
        
        // set up some event handlers to allow for node-dragging
        that.initMouseHandling();
      };
      
      that.redraw = function(){

      function xpt(fromx, fromy, tox, toy,f) {
		    // distance formula
		    //var mag = Math.sqrt((tox - fromx) * (tox-from) + (toy - fromy) * (toy-fromy));
		    // ux = x1 + f * (x2 - x1)
		    // uy = y1 + f * (y2 -y1)
    	    // vector math
		    var ux = fromx + f * (tox - fromx);
		    var uy = fromy + f * (toy - fromy);
		    
		    return {'x':ux,'y':uy};
      }
    	// length of head in pixels
	  function canvas_arrow(ctx, fromx, fromy, tox, toy,headlen){
		    var angle = Math.atan2(toy-fromy,tox-fromx);
		    ctx.moveTo(fromx, fromy);
		    ctx.lineTo(tox, toy);
		    
		    // draw arrow head 2/3 toward node
		    var u = xpt(fromx,fromy,tox,toy,0.95);
		    ctx.moveTo(u.x, u.y);
		    ctx.lineTo(u.x-headlen*Math.cos(angle-Math.PI/6),u.y-headlen*Math.sin(angle-Math.PI/6));
		    ctx.moveTo(u.x, u.y);
		    ctx.lineTo(u.x-headlen*Math.cos(angle+Math.PI/6),u.y-headlen*Math.sin(angle+Math.PI/6));
		}  
	  
	  function computeNodeColor(nodeData) {
		  if(nodeData.isExternal) {
			  return 'blue';
		  }
		  else if(!nodeData.exists) {
			  return 'red';
		  }
		  // internal and exists
		  else {
			  return 'green';
		  }
	  }
        // 
        // redraw will be called repeatedly during the run whenever the node positions
        // change. the new positions for the nodes can be accessed by looking at the
        // .p attribute of a given node. however the p.x & p.y values are in the coordinates
        // of the particle system rather than the screen. you can either map them to
        // the screen yourself, or use the convenience iterators .eachNode (and .eachEdge)
        // which allow you to step through the actual node objects but also pass an
        // x,y point in the screen's coordinate system
        // 
        ctx.fillStyle = "white";
        ctx.fillRect(0,0, canvas.width, canvas.height);
        
        particleSystem.eachEdge(function(edge, pt1, pt2){
          // edge: {source:Node, target:Node, length:#, data:{}}
          // pt1:  {x:#, y:#}  source position in screen coords
          // pt2:  {x:#, y:#}  target position in screen coords

          // draw a line from pt1 to pt2
          ctx.strokeStyle = "rgba(0,0,0, .75)";
          ctx.lineWidth = 1;
          ctx.beginPath();
          
          canvas_arrow(ctx,pt1.x,pt1.y,pt2.x,pt2.y,15);

         // ctx.moveTo(pt1.x, pt1.y);
         // ctx.lineTo(pt2.x, pt2.y);
          ctx.stroke();
        });

        particleSystem.eachNode(function(node, pt){
          // node: {mass:#, p:{x,y}, name:"", data:{}}
          // pt:   {x:#, y:#}  node position in screen coords

          // draw a rectangle centered at pt
          //var w = 10;
          //ctx.fillStyle = (node.data.alone) ? "orange" : "black";
          //ctx.fillRect(pt.x-w/2, pt.y-w/2, w,w);
            var w = Math.max(20, 20+gfx.textWidth(node.name) );
            //if (node.data.alpha===0) return
           // if (node.data.shape=='dot'){
              gfx.oval(pt.x-w/2, pt.y-w/2, w, w, {fill:computeNodeColor(node.data),alpha:'0.67'}); //{fill:node.data.color, alpha:node.data.alpha});
              gfx.text(node.name, pt.x, pt.y+7, {color:"white", align:"center", font:"Arial", size:12});
              gfx.text(node.name, pt.x, pt.y+7, {color:"white", align:"center", font:"Arial", size:12});
           // }else{
           //   gfx.rect(pt.x-w/2, pt.y-8, w, 20, 4, {fill:node.data.color, alpha:node.data.alpha})
           //   gfx.text(node.name, pt.x, pt.y+9, {color:"white", align:"center", font:"Arial", size:12})
           //   gfx.text(node.name, pt.x, pt.y+9, {color:"white", align:"center", font:"Arial", size:12})
          //  }
        }); 			
      };
      
      that.initMouseHandling = function(){
        // no-nonsense drag and drop (thanks springy.js)
        var dragged = null;

        // set up a handler object that will initially listen for mousedowns then
        // for moves and mouseups while dragging
        var handler = {
          clicked:function(e){
            var pos = $(canvas).offset();
            _mouseP = arbor.Point(e.pageX-pos.left, e.pageY-pos.top);
            dragged = particleSystem.nearest(_mouseP);

            if (dragged && dragged.node !== null){
              // while we're dragging, don't let physics move the node
              dragged.node.fixed = true;
            }

            $(canvas).bind('mousemove', handler.dragged);
            $(window).bind('mouseup', handler.dropped);

            return false;
          },
          dragged:function(e){
            var pos = $(canvas).offset();
            var s = arbor.Point(e.pageX-pos.left, e.pageY-pos.top);

            if (dragged && dragged.node !== null){
              var p = particleSystem.fromScreen(s);
              dragged.node.p = p;
            }

            return false;
          },

          dropped:function(e){
            if (dragged===null || dragged.node===undefined) return;
            if (dragged.node !== null) dragged.node.fixed = false;
            dragged.node.tempMass = 1000;
            dragged = null;
            $(canvas).unbind('mousemove', handler.dragged);
            $(window).unbind('mouseup', handler.dropped);
            _mouseP = null;
            return false;
          }
        }
        
        // start listening
        $(canvas).mousedown(handler.clicked);

      };
      
    return that;
  }; 
  
  function getGraphData(cb) {
	  $.getJSON( "data/gamesalutes.json", function( data ) {
		  cb(data.nodes);
	  });
  }
  function populateGraph(sys,dataNodes) {
	  // id -> node:
		//	  "id": "35",
		//      "value": "/nvisia-careers/benefits",
		//      "name": "Benefits",
		//      "edges": [],
		//      "isExternal": false,
		//      "exists": true
	  // first create the nodes
	  var nodeMap = {};
	  
	  // nodes
	  for(id in dataNodes) {
		  if(dataNodes.hasOwnProperty(id)) {
			  var node = dataNodes[id];
			  var graphNode = sys.addNode(node.name,node);
			  nodeMap[id] = graphNode;
		  }
	  }
	  //edges
	  for(id in dataNodes) {
		  if(dataNodes.hasOwnProperty(id)) {
			  var node = dataNodes[id];
			  var graphFromNode = nodeMap[id];
			  
			  // add edges
			  for(var i = 0; i < node.edges.length; ++i) {
				  var toNodeId = node.edges[i];
				  
				  var toNode = dataNodes[toNodeId];
				  if(toNode != null) {
					  var graphToNode = nodeMap[toNodeId];
					  sys.addEdge(graphFromNode,graphToNode);
				  }
				  else {
					  console.log("No node value for to edge=" + toNodeId + "fromNode=" + JSON.stringify(node));
				  }
			  }
		  }
	  }
  }
  
  function computeWindowSize() {
	  
		var height = parseInt($(document).height(),10);
		var width = parseInt($(document).width(),10);
			
		$('#viewport').attr('width',width);
		$('#viewport').attr('height',height);
  }
  $(document).ready(function(){
	  
	computeWindowSize();
	
	$( window ).resize(computeWindowSize);
	
    var sys = arbor.ParticleSystem(1000, 600, 0.5); // create the system with sensible repulsion/stiffness/friction
    sys.parameters({gravity:true}); // use center-gravity to make the graph settle nicely (ymmv)
    sys.renderer = renderer("#viewport"); // our newly created renderer will have its .init() method called shortly by sys...

    getGraphData(function(data) {
    	populateGraph(sys,data);
    });
    // add some nodes to the graph and watch it go...
//    sys.addEdge('a','b');
//    sys.addEdge('a','c');
//    sys.addEdge('a','d');
//    sys.addEdge('a','e');
    //sys.addNode('f', {alone:true, mass:.25});

    // or, equivalently:
    //
    // sys.graft({
    //   nodes:{
    //     f:{alone:true, mass:.25}
    //   }, 
    //   edges:{
    //     a:{ b:{},
    //         c:{},
    //         d:{},
    //         e:{}
    //     }
    //   }
    // })
    
  });

})(this.jQuery);