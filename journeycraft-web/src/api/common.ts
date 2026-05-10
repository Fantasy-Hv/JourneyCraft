import { getApiMode } from '@/utils/api-mode'

export async function invoke<T>(mockAction: () => Promise<T>, realAction: () => Promise<T>) {
  return getApiMode() === 'mock' ? mockAction() : realAction()
}

