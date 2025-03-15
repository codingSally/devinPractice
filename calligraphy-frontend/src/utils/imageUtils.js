/**
 * Converts a regular image URL to a cartoon-style image URL
 * Uses cute cartoon animal images stored locally in the public directory
 * @param {string} imageUrl - The original image URL
 * @returns {string} - The local cartoon animal image URL
 */
export function getCartoonImage(imageUrl) {
  // Using local images stored in the public directory
  // These include product images and cute cartoon animal images
  
  // First, randomly decide whether to use a product image (higher probability)
  const useProductImage = Math.random() < 0.7; // 70% chance to use product images
  
  if (useProductImage) {
    // Use one of the product images
    const productImages = [
      '/images/products/product1.png',
      '/images/products/product2.png'
    ];
    const randomIndex = Math.floor(Math.random() * productImages.length);
    return productImages[randomIndex];
  }
  
  // Otherwise use animal images based on category
  let animal;
  
  if (imageUrl && imageUrl.includes('brush')) {
    animal = 'fox'; // Fox for brush category
  } else if (imageUrl && imageUrl.includes('ink')) {
    animal = 'cat'; // Cat for ink category
  } else if (imageUrl && imageUrl.includes('paper')) {
    animal = 'panda'; // Panda for paper category
  } else if (imageUrl && imageUrl.includes('calligraphy')) {
    animal = 'rabbit'; // Rabbit for calligraphy works category
  } else {
    animal = 'bear'; // Bear as default
  }
  
  // Return the path to the local image in the public directory
  return `/images/${animal}.jpg`;
}
