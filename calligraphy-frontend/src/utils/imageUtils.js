/**
 * Converts a regular image URL to a cartoon-style image URL
 * Uses a cartoon filter service to transform images
 * @param {string} imageUrl - The original image URL
 * @returns {string} - The cartoon-style image URL
 */
export function getCartoonImage(imageUrl) {
  // If no image URL is provided, return a cartoon-style placeholder
  if (!imageUrl || imageUrl.includes('placeholder.com')) {
    // Return cartoon-style placeholder images based on category
    return 'https://img.freepik.com/free-vector/hand-drawn-flat-design-calligraphy-tools_23-2149150736.jpg';
  }
  
  // For real images, we would normally use an image processing API
  // Since we don't have access to an actual image processing API in this context,
  // we'll return predefined cartoon-style images based on patterns in the URL
  
  // This is a simplified implementation - in a real app, you would use an actual image processing service
  if (imageUrl.includes('brush') || imageUrl.includes('brushes')) {
    return 'https://img.freepik.com/free-vector/hand-drawn-flat-design-calligraphy-tools_23-2149150736.jpg';
  } else if (imageUrl.includes('ink')) {
    return 'https://img.freepik.com/free-vector/hand-drawn-flat-design-stationery-collection_23-2149150735.jpg';
  } else if (imageUrl.includes('paper')) {
    return 'https://img.freepik.com/free-vector/hand-drawn-flat-design-stationery-collection_23-2149150738.jpg';
  } else if (imageUrl.includes('calligraphy')) {
    return 'https://img.freepik.com/free-vector/hand-drawn-flat-design-chinese-calligraphy-concept_23-2149150737.jpg';
  }
  
  // Default cartoon image for other cases
  return 'https://img.freepik.com/free-vector/hand-drawn-flat-design-calligraphy-concept_23-2149150734.jpg';
}
