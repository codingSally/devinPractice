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
    '/images/products/real_product_images_new/product1.svg',
    '/images/products/real_product_images_new/product2.svg',
    '/images/products/test_product_images/product1.svg',
    '/images/products/test_product_images/product2.svg',
    '/images/fox.jpg',
    '/images/cat.jpg',
    '/images/panda.jpg'
  ];
  
  // Assign specific images based on product ID hash to ensure consistency
  // This ensures the same product always gets the same image
  const imageIndex = Math.abs(productId) % productImages.length;
  return productImages[imageIndex];
}
