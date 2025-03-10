/**
 * Converts a regular image URL to a cartoon-style image URL
 * Uses cute cartoon animal images stored locally in the public directory
 * @param {string} imageUrl - The original image URL
 * @returns {string} - The local cartoon animal image URL
 */
export function getCartoonImage(imageUrl) {
  // Using local images stored in the public directory
  // These are cute cartoon animal images that match the style requested by the user
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
