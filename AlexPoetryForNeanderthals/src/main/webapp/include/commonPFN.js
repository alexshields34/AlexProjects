/**
 * @param x1 An integer.
 * @param x2 An integer.
 * @param y1 An integer.
 * @param y2 An integer.
 */
function findDistance(x1, x2, y1, y2) {
   let result;
   
   result=Math.pow((x2-x1), 2) + Math.pow((y2-y1), 2) ;
   result=Math.sqrt(result);
   
   return result;
}


/**
 * Return the absolute position of the element on its web page.
 * @param element An HTML element in a web page, in particular an img element.
 */
function findCenterPoint(element) {

    let upperLeftCoord;
    let centerPoint = {x:0, y:0};
    
    upperLeftCoord=getAbsolutePosition(element);
    
    centerPoint.x=(upperLeftCoord.offsetLeft + (element.clientWidth/2));
    centerPoint.y=(upperLeftCoord.offsetTop + (element.clientHeight/2));
    
    return centerPoint;
}


/**
 * Get the absolute top left corner of an element.
 *
 * Returns an object with the variables offsetLeft and offsetTop.
 *
 * From this site:
 * https://stackoverflow.com/questions/9040768/getting-coordinates-of-objects-in-js
 *
 */
function getAbsolutePosition(elem){

    let dims = {offsetLeft:0, offsetTop:0};

    do {
        dims.offsetLeft += elem.offsetLeft;
        dims.offsetTop += elem.offsetTop;
    }

    while (elem = elem.offsetParent);

    return dims;
}




function isString(x) {
	return Object.prototype.toString.call(x) === "[object String]"
}

function startsWithUpperCaseLetter(inputString)
{
	let retVal=false;
	let charCode = inputString.charCodeAt(0);

	if(charCode >= "A".charCodeAt(0) && charCode <= "Z".charCodeAt(0)){
		retVal=true;
	}
	
	return retVal;
}


/**
 * Parse seconds.
 *
 * @param wholeSeconds This is in seconds, and it may not be null.
 * @return Return a string which is minutes:seconds.
 */
function parseSeconds(wholeSeconds)
{
	let minutes, seconds, retVal,
	    secondsAsString;
	
	minutes=Math.trunc(wholeSeconds / 60);
	seconds=wholeSeconds % 60;
		
	if (seconds>=10) {
		secondsAsString=""+seconds;
	} else {
		secondsAsString="0"+seconds;
	}
		
	retVal=minutes+":"+secondsAsString;
	
	return retVal;
}


function setTimeElement(elementId, seconds)
{
    let parsedTime, element;
    
    element=document.getElementById(elementId);
    
    parsedTime=parseSeconds(seconds);
    
    element.innerHTML=parsedTime; 
}


function returnFalse()
{
	return false;
}

/**
 * This function was found on:
 * https://plainjs.com/javascript/styles/get-the-position-of-an-element-relative-to-the-document-24/
 * 
 * Return the stuff that's normally in getBoundingClientRect
 */
function offsetRelativeToDocument(el) {
    let retVal, rect, scrollLeft, scrollTop;
    
    rect = el.getBoundingClientRect(),
    scrollLeft = window.pageXOffset || document.documentElement.scrollLeft,
    scrollTop = window.pageYOffset || document.documentElement.scrollTop;
    
    retVal={ "top": rect.top + scrollTop,
    	 "left": rect.left + scrollLeft,
    	 "right": rect.right + scrollLeft,
    	 "bottom" : rect.bottom + scrollTop,
    	 "height" : rect.height,
    	 "width" :  rect.width,
    	 "x" : rect.left + scrollLeft,
    	 "y" : rect.top + scrollTop};
    return retVal;


    
//// example use
//var div = document.querySelector('div');
//var divOffset = offsetRelativeToDocument(div);
//console.log(divOffset.left, divOffset.top);
    
}


/**
 * Copied from word-twist.js.
 *
 * Determines the distance from a center between 2 points (in our case: mouse location and cube limits)
 * @param dot1 An array of two integers.
 * @param dot2 An array of two integers.
 */
function distance(dot1, dot2) {
			let x1 = dot1[0],
				y1 = dot1[1],
				x2 = dot2[0],
				y2 = dot2[1];
				
	return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
}

/**
 * Return true if the needle is within the array.
 *
 * Copied from https://stackoverflow.com/questions/6116474/how-to-find-if-an-array-contains-a-specific-string-in-javascript-jquery
 */
function arrayContains(arrayHaystack, needle)
{
    return (arrayHaystack.indexOf(needle) > -1);
}


/**
 * @return null if a parent of the specified type can't be found.
 */
function getParentElementByType(el, elementTypeNameLowerCase)
{
	let parent;
	
	parent = el.parentElement;
	while (parent && parent.tagName.toLowerCase() != elementTypeNameLowerCase) {
		parent=parent.parentElement;
	}
	
	return parent;
}

/**
 * Copies attributes from one element to another.
 * Example:
 * 
 * image = document.getElementById("storedImage");
 * newImage = document.createElement("img");
 * newImage.innerHTML=image.innerHTML;
 * copyAttributes(image, newImage);
 */
function copyAttributes(source, target) {
  return Array.from(source.attributes).forEach(attribute => {
    target.setAttribute(
      attribute.nodeName === 'id' ? 'data-id' : attribute.nodeName,
      attribute.nodeValue
    );
  });
}


/**
 * Return true if brChild overlaps or is within brContainer.
 * I should probably make this use the center point of the bounding rect.
 *
 * @param brContainer The value of getBoundingClientRect() of a div or such container.
 * @param brChild  The value of getBoundingClientRect() of any element.
 * @return true if the brChild overlaps or is within brContainer.
 */
function containsOrOverlaps(brContainer, brChild) {
	// The code below is the contain and overlap parts of this code:
 	// https://stackoverflow.com/questions/59498298/javascript-detect-if-dom-element-overlaping-inside-or-ouside-with-dom-element
 	
 	let contains, overlaps;
	
	contains = overlaps = false;
	if (brChild.left >= brContainer.left
			&& brChild.top >= brContainer.top
			&& brChild.bottom <= brContainer.bottom
			&& brChild.right <= brContainer.right)
	{
		contains = true;
	}
 	
	if (!contains) {
	 	if (
	    	/* Does container left or right edge pass through element? */
	    	(brChild.left < brContainer.left && brChild.right > brContainer.left) ||
	    	(brChild.left < brContainer.right && brChild.right > brContainer.right) ||
	    	/* Does container top or bottom edge pass through element? */
		    (brChild.top < brContainer.top && brChild.bottom > brContainer.top) ||
	    	(brChild.top < brContainer.bottom && brChild.bottom > brContainer.bottom))
	 	{
		    overlaps = true;
		}
	}
	
	return contains || overlaps;
}

/**
 * @param container A div, for example.
 * @param collection An array.
 * @return An array of HTML objects.
 */
function getContainedOrOverlappingObjects(container, collection)
{
	let matchingChildren = [];
	
	for (const child of collection) {
		if (containsOrOverlaps(container.getBoundingClientRect(), child.getBoundingClientRect())) {
			matchingChildren.push(child);
		}
	}
	
	return matchingChildren;
}

/**
 * Create an element of the same type as originalElement,
 * and copy attributes using the copyAttributes function.
 * @param originalElement HTML dom object.
 */
function duplicateElement(originalElement, shouldCopyInnerHTML = false)
{
	let newElement;
   
   	newElement = document.createElement(originalElement.tagName.toLowerCase());
   
   	copyAttributes(originalElement, newElement);
   
   	if (shouldCopyInnerHTML) {
		newElement.innerHTML = originalElement.innerHTML;
   	}
   
   	return newElement;
}


function getRandomInt(min, max) {
  return Math.floor(Math.random() * max) + min;
}

function getListSize(object) {
	return Object.keys(object).length;
}

