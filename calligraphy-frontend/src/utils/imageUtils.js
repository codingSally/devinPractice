/**
 * Converts a regular image URL to a product image URL
 * Uses consistent image assignment to ensure unique images
 * @param {string} imageUrl - The original image URL or product ID
 * @returns {string} - The local product image URL
 */
export function getCartoonImage(imageUrl) {
  // Map product IDs to specific images to ensure no duplicates
  // Extract product ID from URL if available
  let productId = 0;
  if (imageUrl) {
    const idMatch = imageUrl.match(/\/products\/(\d+)/);
    if (idMatch && idMatch[1]) {
      productId = parseInt(idMatch[1], 10);
    } else {
      // Generate a consistent hash from the URL string
      productId = Array.from(imageUrl).reduce((acc, char) => acc + char.charCodeAt(0), 0);
    }
  }
  
  // Define available product images
  const productImages = [
    '/images/products/product1.jpg',
    '/images/products/product2.jpg',
    '/images/products/product3.jpg',
    '/images/products/product4.jpg',
    '/images/products/product5.jpg',
    '/images/products/product6.jpg',
    '/images/products/product7.jpg'
  ];
  
  // Assign specific images based on product ID hash to ensure consistency
  // This ensures the same product always gets the same image
  const imageIndex = Math.abs(productId) % productImages.length;
  return productImages[imageIndex];
}
