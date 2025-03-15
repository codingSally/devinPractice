/**
 * Converts a regular image URL to a cartoon-style image URL
 * Uses cute cartoon animal images stored locally in the public directory
 * @param {string} imageUrl - The original image URL
 * @returns {string} - The local cartoon animal image URL
 */
export function getCartoonImage(imageUrl) {
  // Using local images stored in the public directory
  // These include product images and cute cartoon animal images
  
  // Map product IDs to specific images to ensure no duplicates
  // Extract product ID from URL if available
  let productId = 0;
  if (imageUrl) {
    const idMatch = imageUrl.match(/\/products\/(\d+)/);
    if (idMatch && idMatch[1]) {
      productId = parseInt(idMatch[1], 10);
    } else {
      // Generate a consistent hash from the URL string
      productId = Array.from(imageUrl).reduce((acc, char) => acc + char.charCodeAt(0), 0) % 7;
    }
  }
  
  // Assign specific images based on product ID or hash to ensure consistency
  // This ensures the same product always gets the same image
  switch (productId % 7) {
    case 0:
      return '/images/products/product1.png';
    case 1:
      return '/images/products/product2.png';
    case 2:
      return '/images/fox.jpg';
    case 3:
      return '/images/cat.jpg';
    case 4:
      return '/images/panda.jpg';
    case 5:
      return '/images/rabbit.jpg';
    case 6:
      return '/images/bear.jpg';
    default:
      return '/images/products/product1.png';
  }
}
